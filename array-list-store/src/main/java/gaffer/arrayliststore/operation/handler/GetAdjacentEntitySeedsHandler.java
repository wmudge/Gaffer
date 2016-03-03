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

package gaffer.arrayliststore.operation.handler;

import gaffer.arrayliststore.ArrayListStore;
import gaffer.data.element.Edge;
import gaffer.operation.OperationException;
import gaffer.operation.data.EntitySeed;
import gaffer.operation.impl.get.GetAdjacentEntitySeeds;
import gaffer.store.Store;
import gaffer.store.operation.handler.OperationHandler;

import java.util.ArrayList;
import java.util.List;

import static gaffer.operation.GetOperation.IncludeIncomingOutgoingType.INCOMING;
import static gaffer.operation.GetOperation.IncludeIncomingOutgoingType.OUTGOING;

public class GetAdjacentEntitySeedsHandler implements OperationHandler<GetAdjacentEntitySeeds, Iterable<EntitySeed>> {
    @Override
    public Iterable<EntitySeed> doOperation(final GetAdjacentEntitySeeds operation, final Store store) throws OperationException {
        return doOperation(operation, (ArrayListStore) store);
    }

    private List<EntitySeed> doOperation(final GetAdjacentEntitySeeds operation, final ArrayListStore store) {
        final EntitySeed[] reuseableTuple = new EntitySeed[2];
        final List<EntitySeed> result = new ArrayList<>();
        for (final Edge edge : store.getEdges()) {
            if (operation.validateFlags(edge)) {
                extractOtherEndOfSeededEdge(edge, operation, reuseableTuple);
                if ((null != reuseableTuple[0] || null != reuseableTuple[1]) && operation.validateFilter(edge)) {
                    if (null != reuseableTuple[0]) {
                        result.add(reuseableTuple[0]);
                    }
                    if (null != reuseableTuple[1]) {
                        result.add(reuseableTuple[1]);
                    }
                }
            }
        }

        return result;
    }

    /**
     * Extracts the vertex at other end of a seeded edge
     *
     * @param edge           the edge to extract the vertex at other end of
     * @param operation      the operation
     * @param reuseableTuple instead of creating an array every time the method is called this array is reused.
     */
    private void extractOtherEndOfSeededEdge(final Edge edge,
                                             final GetAdjacentEntitySeeds operation,
                                             final EntitySeed[] reuseableTuple) {
        reuseableTuple[0] = null;
        reuseableTuple[1] = null;
        boolean matchSource = !edge.isDirected() || !INCOMING.equals(operation.getIncludeIncomingOutGoing());
        boolean matchDestination = !edge.isDirected() || !OUTGOING.equals(operation.getIncludeIncomingOutGoing());

        for (final EntitySeed seed : operation.getSeeds()) {
            if (matchSource && edge.getSource().equals(seed.getVertex())) {
                reuseableTuple[1] = new EntitySeed(edge.getDestination());
                matchSource = false;
                if (!matchDestination) {
                    break;
                }
            }

            if (matchDestination && edge.getDestination().equals(seed.getVertex())) {
                reuseableTuple[0] = new EntitySeed(edge.getSource());
                matchDestination = false;
                if (!matchSource) {
                    break;
                }
            }
        }

        // Don't return duplicate results
        if (reuseableTuple[0] != null && reuseableTuple[1] != null && reuseableTuple[0].equals(reuseableTuple[1])) {
            reuseableTuple[1] = null;
        }
    }
}


