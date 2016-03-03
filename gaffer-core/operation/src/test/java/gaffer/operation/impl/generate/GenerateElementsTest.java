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

package gaffer.operation.impl.generate;

import gaffer.data.generator.ElementGeneratorImpl;
import gaffer.exception.SerialisationException;
import gaffer.jsonserialisation.JSONSerialiser;
import gaffer.operation.OperationTest;
import java.util.Arrays;
import java.util.Iterator;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class GenerateElementsTest implements OperationTest {
    private static final JSONSerialiser serialiser = new JSONSerialiser();

    @Test
    @Override
    public void shouldSerialiseAndDeserialiseOperation() throws SerialisationException {
        // Given
        final GenerateElements<String> op = new GenerateElements<>(Arrays.asList("obj 1", "obj 2"), new ElementGeneratorImpl());

        // When
        byte[] json = serialiser.serialise(op, true);
        final GenerateElements deserialisedOp = serialiser.deserialise(json, GenerateElements.class);

        // Then
        final Iterator itr = deserialisedOp.getInput().iterator();
        assertEquals("obj 1", itr.next());
        assertEquals("obj 2", itr.next());
        assertFalse(itr.hasNext());

        assertTrue(deserialisedOp.getElementGenerator() instanceof ElementGeneratorImpl);
    }
}
