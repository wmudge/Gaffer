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
package gaffer.function.simple.filter;

import gaffer.exception.SerialisationException;
import gaffer.function.FilterFunctionTest;
import gaffer.jsonserialisation.JSONSerialiser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class IsTrueTest extends FilterFunctionTest {
    @Test
    public void shouldAcceptTheValueWhenTrue() {
        // Given
        final IsTrue filter = new IsTrue();

        // When
        boolean accepted = filter._isValid(true);

        // Then
        assertTrue(accepted);
    }

    @Test
    public void shouldAcceptTheValueWhenObjectTrue() {
        // Given
        final IsTrue filter = new IsTrue();

        // When
        boolean accepted = filter._isValid(Boolean.TRUE);

        // Then
        assertTrue(accepted);
    }

    @Test
    public void shouldRejectTheValueWhenNull() {
        // Given
        final IsTrue filter = new IsTrue();

        // When
        boolean accepted = filter._isValid(null);

        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldRejectTheValueWhenNullItemInArray() {
        // Given
        final IsTrue filter = new IsTrue();

        // When
        boolean accepted = filter._isValid(null);

        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldRejectTheValueWhenFalse() {
        // Given
        final IsTrue filter = new IsTrue();

        // When
        boolean accepted = filter._isValid(false);

        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldClone() {
        // Given
        final IsTrue filter = new IsTrue();

        // When
        final IsTrue clonedFilter = filter.statelessClone();

        // Then
        assertNotSame(filter, clonedFilter);
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final IsTrue filter = new IsTrue();

        // When
        final String json = new String(new JSONSerialiser().serialise(filter, true));

        // Then
        assertEquals("{\n" +
                "  \"class\" : \"gaffer.function.simple.filter.IsTrue\"\n" +
                "}", json);

        // When 2
        final IsTrue deserialisedFilter = new JSONSerialiser().deserialise(json.getBytes(), IsTrue.class);

        // Then 2
        assertNotNull(deserialisedFilter);
    }

    @Override
    protected Class<IsTrue> getFunctionClass() {
        return IsTrue.class;
    }

    @Override
    protected IsTrue getInstance() {
        return new IsTrue();
    }

    @Override
    protected Object[] getSomeAcceptedInput() {
        return new Object[]{true};
    }
}
