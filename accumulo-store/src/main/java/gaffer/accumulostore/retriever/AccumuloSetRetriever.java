/*
 * Copyright 2016 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package gaffer.accumulostore.retriever;

import gaffer.accumulostore.AccumuloStore;
import gaffer.accumulostore.key.exception.AccumuloElementConversionException;
import gaffer.accumulostore.key.exception.IteratorSettingException;
import gaffer.accumulostore.key.exception.RangeFactoryException;
import gaffer.accumulostore.retriever.impl.AccumuloSingleIDRetriever;
import gaffer.accumulostore.utils.BloomFilterUtils;
import gaffer.accumulostore.utils.CloseableIterator;
import gaffer.data.element.Edge;
import gaffer.data.element.Element;
import gaffer.data.element.Entity;
import gaffer.operation.GetOperation;
import gaffer.operation.data.EntitySeed;
import gaffer.store.StoreException;
import org.apache.accumulo.core.client.BatchScanner;
import org.apache.accumulo.core.client.IteratorSetting;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Range;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.util.bloom.BloomFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

public abstract class AccumuloSetRetriever extends AccumuloRetriever<GetOperation<EntitySeed, ?>> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccumuloSetRetriever.class);
    private boolean readEntriesIntoMemory;

    public AccumuloSetRetriever(final AccumuloStore store, final GetOperation<EntitySeed, ?> operation)
            throws StoreException {
        this(store, operation, false);
    }

    public AccumuloSetRetriever(final AccumuloStore store, final GetOperation<EntitySeed, ?> operation,
                                final boolean readEntriesIntoMemory) throws StoreException {
        super(store, operation);
        this.readEntriesIntoMemory = readEntriesIntoMemory;
    }

    public AccumuloSetRetriever(final AccumuloStore store, final GetOperation<EntitySeed, ?> operation,
                                final IteratorSetting... iteratorSettings) throws StoreException {
        this(store, operation, false, iteratorSettings);
    }

    public AccumuloSetRetriever(final AccumuloStore store, final GetOperation<EntitySeed, ?> operation,
                                final boolean readEntriesIntoMemory, final IteratorSetting... iteratorSettings) throws StoreException {
        super(store, operation, iteratorSettings);
        this.readEntriesIntoMemory = readEntriesIntoMemory;
    }

    public void setReadEntriesIntoMemory(final boolean readEntriesIntoMemory) {
        this.readEntriesIntoMemory = readEntriesIntoMemory;
    }

    @Override
    public Iterator<Element> iterator() {
        if (!hasSeeds()) {
            return Collections.emptyIterator();
        }
        if (readEntriesIntoMemory) {
            try {
                iterator = createElementIteratorReadIntoMemory();
            } catch (final RetrieverException e) {
                LOGGER.error(e.getMessage() + " returning empty iterator");
                return Collections.emptyIterator();
            }
        } else {
            try {
                iterator = createElementIteratorFromBatches();
            } catch (final RetrieverException e) {
                LOGGER.error(e.getMessage() + " returning empty iterator");
                return Collections.emptyIterator();
            }
        }
        return iterator;
    }

    protected abstract boolean hasSeeds();

    protected abstract AbstractElementIteratorReadIntoMemory createElementIteratorReadIntoMemory()
            throws RetrieverException;

    protected abstract AbstractElementIteratorFromBatches createElementIteratorFromBatches() throws RetrieverException;

    protected Set<Object> extractVertices(final Iterable<EntitySeed> seeds) {
        final Set<Object> vertices = new HashSet<>();
        for (final EntitySeed seed : seeds) {
            vertices.add(seed.getVertex());
        }

        return vertices;
    }

    protected void addToBloomFilter(final Iterable<Object> vertices, final BloomFilter filter)
            throws RetrieverException {
        for (final Object vertex : vertices) {
            addToBloomFilter(vertex, filter);
        }
    }

    protected void addToBloomFilter(final Iterable<EntitySeed> seeds, final BloomFilter filter1,
                                    final BloomFilter filter2) throws RetrieverException {
        for (final EntitySeed seed : seeds) {
            addToBloomFilter(seed, filter1, filter2);
        }
    }

    protected void addToBloomFilter(final EntitySeed seed, final BloomFilter filter1, final BloomFilter filter2)
            throws RetrieverException {
        addToBloomFilter(seed.getVertex(), filter1);
        addToBloomFilter(seed.getVertex(), filter2);
    }

    private void addToBloomFilter(final Object vertex, final BloomFilter filter) throws RetrieverException {
        try {
            filter.add(new org.apache.hadoop.util.bloom.Key(elementConverter.serialiseVertexForBloomKey(vertex)));
        } catch (final AccumuloElementConversionException e) {
            throw new RetrieverException("Failed to add identifier to the bloom key", e);
        }
    }

    protected abstract class AbstractElementIteratorReadIntoMemory implements CloseableIterator<Element> {
        private AccumuloRetriever<?> parentRetriever;
        private Iterator<Element> iterator;
        private Element nextElm;

        protected void initialise(final BloomFilter filter) throws RetrieverException {
            IteratorSetting elementFilter = null;
            IteratorSetting bloomFilter = null;
            try {
                elementFilter = iteratorSettingFactory.getElementFilterIteratorSetting(operation.getView(), store);
            } catch (final IteratorSettingException e) {
                LOGGER.error(
                        "Failed to apply the element filter to the retriever, creating the gaffer.accumulostore.retriever without the element filter",
                        e);
            }

            try {
                bloomFilter = iteratorSettingFactory.getBloomFilterIteratorSetting(filter);
            } catch (final IteratorSettingException e) {
                LOGGER.error(
                        "Failed to apply the bloom filter to the retriever, creating the gaffer.accumulostore.retriever without bloom filter",
                        e);
            }
            try {
                parentRetriever = new AccumuloSingleIDRetriever(store, operation,
                        iteratorSettingFactory.getEdgeEntityDirectionFilterIteratorSetting(operation), elementFilter,
                        bloomFilter);
            } catch (final StoreException e) {
                throw new RetrieverException(e.getMessage(), e);
            }
            iterator = parentRetriever.iterator();
        }

        @Override
        public boolean hasNext() {
            if (null != nextElm) {
                return true;
            }
            if (null == iterator) {
                throw new IllegalStateException(
                        "This iterator has not been initialised. Call initialise before using it.");
            }
            while (iterator.hasNext()) {
                nextElm = iterator.next();
                if (checkIfBothEndsInSet(nextElm)) {
                    return true;
                }
            }
            return false;
        }

        @Override
        public Element next() {
            if (null == nextElm) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
            }
            Element nextReturn = nextElm;
            nextElm = null;
            doTransformation(nextReturn);
            return nextReturn;

        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Can't remove elements from a graph element iterator");
        }

        @Override
        public void close() {
            if (parentRetriever != null) {
                parentRetriever.close();
            }
        }

        protected abstract boolean checkIfBothEndsInSet(final Object source, final Object destination);

        /**
         * Returns <code>true</code> if either an
         * {@link gaffer.data.element.Entity} or if an
         * {@link gaffer.data.element.Edge} then need both ends to be in the
         * set.
         *
         * @param elm the element to check
         * @return True if the provided element is an edge and Both ends are
         * contained in the provided seed sets or if the element is an
         * entity
         */
        private boolean checkIfBothEndsInSet(final Element elm) {
            if (Entity.class.isInstance(elm)) {
                return true;
            }
            final Edge edge = (Edge) elm;
            final Object source = edge.getSource();
            final Object destination = edge.getDestination();
            return checkIfBothEndsInSet(source, destination);
        }

    }

    protected abstract class AbstractElementIteratorFromBatches implements CloseableIterator<Element> {
        protected Iterator<EntitySeed> idsAIterator;
        // The Bloom filter that is maintained client-side
        // as a secondary defeat of false positives.
        protected BloomFilter clientSideFilter;
        protected Set<Object> currentSeeds;
        protected BatchScanner scanner;
        protected BloomFilter filter;
        private Iterator<Map.Entry<Key, Value>> scannerIterator;
        private Element nextElm;
        private int count;

        public AbstractElementIteratorFromBatches() {
            // Set up client side filter
            clientSideFilter = BloomFilterUtils.getBloomFilter(store.getProperties().getClientSideBloomFilterSize());
            // Create Bloom filter to be passed to iterators.
            filter = BloomFilterUtils.getBloomFilter(store.getProperties().getFalsePositiveRate(),
                    store.getProperties().getMaxEntriesForBatchScanner(),
                    store.getProperties().getMaxBloomFilterToPassToAnIterator());
            currentSeeds = new HashSet<>();
        }

        @Override
        public boolean hasNext() {
            if (null != nextElm) {
                return true;
            }
            try {
                while (_hasNext()) {
                    final Map.Entry<Key, Value> entry = scannerIterator.next();
                    try {
                        nextElm = elementConverter.getFullElement(entry.getKey(), entry.getValue(),
                                operation.getOptions());
                    } catch (final AccumuloElementConversionException e) {
                        LOGGER.error("Failed to create next element from key and value entry set", e);
                        continue;
                    }
                    if (secondaryCheck(nextElm)) {
                        return true;
                    }
                }
            } catch (final RetrieverException e) {
                LOGGER.debug("Failed to retrieve elements into iterator : " + e.getMessage()
                        + " returning iterator has no more elements", e);
                return false;
            }

            return false;
        }

        @Override
        public Element next() {
            if (null == nextElm) {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
            }
            Element nextReturn = nextElm;
            nextElm = null;
            doTransformation(nextReturn);
            return nextReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException(
                    "Can't remove elements from a " + this.getClass().getCanonicalName());
        }

        @Override
        public void close() {
            if (scanner != null) {
                scanner.close();
            }
        }

        protected abstract void updateBloomFilterIfRequired(final EntitySeed seed) throws RetrieverException;

        protected void updateScanner() throws RetrieverException {
            // Read through the first N entities (where N =
            // maxEntriesForBatchScanner), create the associated ranges
            // and add them to a set.
            count = 0;
            final Set<Range> ranges = new HashSet<>();
            while (idsAIterator.hasNext() && count < store.getProperties().getMaxEntriesForBatchScanner()) {
                final EntitySeed seed = idsAIterator.next();
                currentSeeds.add(seed.getVertex());
                count++;
                try {
                    ranges.addAll(rangeFactory.getRange(seed, operation));
                } catch (final RangeFactoryException e) {
                    LOGGER.error("Failed to create a range from given seed", e);
                }
                updateBloomFilterIfRequired(seed);
            }

            try {
                scanner = getScanner(ranges);
            } catch (TableNotFoundException | StoreException e) {
                throw new RetrieverException(e);
            }
            try {
                scanner.addScanIterator(iteratorSettingFactory.getBloomFilterIteratorSetting(filter));
            } catch (final IteratorSettingException e) {
                LOGGER.error("Failed to apply the bloom filter iterator setting continuing without bloom filter", e);
            }
            final IteratorSetting edgeEntitySetting = iteratorSettingFactory
                    .getEdgeEntityDirectionFilterIteratorSetting(operation);
            if (edgeEntitySetting != null) {
                scanner.addScanIterator(edgeEntitySetting);
            }
            IteratorSetting elementFilterSetting = null;
            try {
                elementFilterSetting = iteratorSettingFactory.getElementFilterIteratorSetting(operation.getView(),
                        store);
            } catch (final IteratorSettingException e) {
                LOGGER.error("Error creating filter iterator continuing query without filter");
            }
            if (elementFilterSetting != null) {
                scanner.addScanIterator(elementFilterSetting);
            }
            scannerIterator = scanner.iterator();
        }

        /**
         * Check whether this is valid, i.e. one end is in the current set of
         * seeds that are being queried for and the other matches the Bloom
         * filter (i.e. the client side Bloom filter that is being used as a
         * secondary defeat of false positives).
         *
         * @param elm the element to check
         * @return true if the element matches the seeds, otherwise false
         */
        protected boolean secondaryCheck(final Element elm) {
            if (Entity.class.isInstance(elm)) {
                return true;
            }
            final Edge edge = (Edge) elm;
            final Object source = edge.getSource();
            final Object destination = edge.getDestination();
            final boolean sourceIsInCurrent = currentSeeds.contains(source);
            final boolean destIsInCurrent = currentSeeds.contains(destination);
            boolean sourceMatchesClientFilter;
            try {
                sourceMatchesClientFilter = clientSideFilter.membershipTest(
                        new org.apache.hadoop.util.bloom.Key(elementConverter.serialiseVertexForBloomKey(source)));
            } catch (final AccumuloElementConversionException e) {
                return false;
            }
            boolean destMatchesClientFilter;
            try {
                destMatchesClientFilter = clientSideFilter.membershipTest(
                        new org.apache.hadoop.util.bloom.Key(elementConverter.serialiseVertexForBloomKey(destination)));
            } catch (final AccumuloElementConversionException e) {
                return false;
            }
            return (sourceIsInCurrent && destMatchesClientFilter) || (destIsInCurrent && sourceMatchesClientFilter)
                    || (sourceIsInCurrent && destIsInCurrent);
        }

        private boolean _hasNext() throws RetrieverException {
            // If current scanner has next then return true.
            if (scannerIterator.hasNext()) {
                return true;
            }
            // If current scanner is spent then go back to the iterator
            // through the provided entities, and see if there are more.
            // If so create the next scanner, if there are no more entities
            // then return false.
            while (idsAIterator.hasNext() && !scannerIterator.hasNext()) {
                updateScanner();
            }
            if (!scannerIterator.hasNext()) {
                scanner.close();
            }
            return scannerIterator.hasNext();
        }
    }
}
