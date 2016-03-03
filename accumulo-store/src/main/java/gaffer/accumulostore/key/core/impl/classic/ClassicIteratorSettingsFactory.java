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
package gaffer.accumulostore.key.core.impl.classic;

import org.apache.accumulo.core.client.IteratorSetting;

import gaffer.accumulostore.key.core.AbstractCoreKeyIteratorSettingsFactory;
import gaffer.accumulostore.operation.AbstractRangeOperation;
import gaffer.accumulostore.utils.AccumuloStoreConstants;
import gaffer.accumulostore.utils.IteratorSettingBuilder;
import gaffer.operation.GetOperation;
import gaffer.operation.GetOperation.IncludeEdgeType;
import gaffer.operation.GetOperation.IncludeIncomingOutgoingType;

public class ClassicIteratorSettingsFactory extends AbstractCoreKeyIteratorSettingsFactory {
    private static final String EDGE_DIRECTED_UNDIRECTED_FILTER = ClassicEdgeDirectedUndirectedFilterIterator.class
            .getName();
    private static final String RANGE_ELEMENT_PROPERTY_FILTER_ITERATOR = ClassicRangeElementPropertyFilterIterator.class
            .getName();

    @Override
    public IteratorSetting getEdgeEntityDirectionFilterIteratorSetting(final GetOperation<?, ?> operation) {
        if (operation.getIncludeIncomingOutGoing() == IncludeIncomingOutgoingType.BOTH
                && operation.getIncludeEdges() == IncludeEdgeType.ALL) {
            return null;
        }

        return new IteratorSettingBuilder(
                AccumuloStoreConstants.EDGE_ENTITY_DIRECTED_UNDIRECTED_INCOMING_OUTGOING_FILTER_ITERATOR_PRIORITY,
                AccumuloStoreConstants.EDGE_ENTITY_DIRECTED_UNDIRECTED_INCOMING_OUTGOING_FILTER_ITERATOR_NAME,
                EDGE_DIRECTED_UNDIRECTED_FILTER).includeIncomingOutgoing(operation.getIncludeIncomingOutGoing())
                        .includeEdges(operation.getIncludeEdges()).includeEntities(operation.isIncludeEntities())
                        .build();
    }

    @Override
    public IteratorSetting getElementPropertyRangeQueryFilter(final AbstractRangeOperation<?, ?> operation) {
        final boolean includeEntities = operation.isIncludeEntities();
        final IncludeEdgeType includeEdgeType = operation.getIncludeEdges();
        if (includeEdgeType != IncludeEdgeType.NONE && includeEntities) {
            return null;
        }
        return new IteratorSettingBuilder(AccumuloStoreConstants.RANGE_ELEMENT_PROPERTY_FILTER_ITERATOR_PRIORITY,
                AccumuloStoreConstants.RANGE_ELEMENT_PROPERTY_FILTER_ITERATOR_NAME, RANGE_ELEMENT_PROPERTY_FILTER_ITERATOR).all()
                        .includeEdges(includeEdgeType).includeEntities(includeEntities).build();
    }

}
