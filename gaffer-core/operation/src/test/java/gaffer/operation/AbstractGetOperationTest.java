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

package gaffer.operation;

import gaffer.data.element.Element;
import gaffer.operation.data.ElementSeed;
import gaffer.operation.data.EntitySeed;
import gaffer.data.elementdefinition.view.View;
import gaffer.exception.SerialisationException;
import gaffer.jsonserialisation.JSONSerialiser;
import gaffer.operation.impl.GetOperationImpl;
import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


public class AbstractGetOperationTest implements OperationTest {
    private static final JSONSerialiser serialiser = new JSONSerialiser();

    @Test
    public void shouldCopyFieldsFromGivenOperationWhenConstructing() {
        // Given
        final GetOperation<ElementSeed, ?> operationToCopy = mock(GetOperation.class);
        final View view = mock(View.class);
        final GetOperation.IncludeEdgeType includeEdges = GetOperation.IncludeEdgeType.ALL;
        final boolean includeEntities = true;
        final boolean populateProperties = true;
        final Iterable<ElementSeed> input = mock(Iterable.class);

        given(operationToCopy.getView()).willReturn(view);
        given(operationToCopy.getIncludeEdges()).willReturn(includeEdges);
        given(operationToCopy.isIncludeEntities()).willReturn(includeEntities);
        given(operationToCopy.isPopulateProperties()).willReturn(populateProperties);
        given(operationToCopy.getInput()).willReturn(input);

        // When
        final GetOperation<ElementSeed, Element> operation = new GetOperationImpl<>(operationToCopy);

        // Then
        assertSame(view, operation.getView());
        assertSame(includeEdges, operation.getIncludeEdges());
        assertEquals(includeEntities, operation.isIncludeEntities());
        assertSame(input, operation.getInput());
        assertEquals(populateProperties, operation.isPopulateProperties());
    }

    @Test
    @Override
    public void shouldSerialiseAndDeserialiseOperation() throws SerialisationException {
        // Given
        final String identifier = "identifier";
        final ElementSeed input = new EntitySeed(identifier);
        final GetOperationImpl<ElementSeed, Element> op = new GetOperationImpl<>(Collections.singletonList(input));

        // When
        byte[] json = serialiser.serialise(op, true);
        final GetOperationImpl<ElementSeed, Element> deserialisedOp = serialiser.deserialise(json, GetOperationImpl.class);

        // Then
        assertNotNull(deserialisedOp);
        assertEquals(identifier, ((EntitySeed) deserialisedOp.getInput().iterator().next()).getVertex());
    }
}
