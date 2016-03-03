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

public class AreEqualTest extends FilterFunctionTest {

    @Test
    public void shouldAcceptTheWhenEqualValues() {

        final AreEqual equals = new AreEqual();

        boolean accepted = equals._isValid(new String[]{"test", "test"});

        assertTrue(accepted);
    }

    @Test
    public void shouldAcceptWhenAllNull() {

        final AreEqual equals = new AreEqual();

        boolean accepted = equals._isValid(new String[]{null, null});

        assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenOneIsNull() {

        final AreEqual equals = new AreEqual();

        boolean accepted = equals._isValid(new String[]{null, "test"});

        assertFalse(accepted);
    }

    @Test
    public void shouldRejectWhenNotEqual() {

        final AreEqual equals = new AreEqual();

        boolean accepted = equals._isValid(new String[]{"test", "test2"});

        assertFalse(accepted);
    }

    @Test
    public void shouldClone() {
        // Given
        final AreEqual filter = new AreEqual();

        // When
        final AreEqual clonedFilter = filter.statelessClone();

        // Then
        assertNotSame(filter, clonedFilter);
        assertNotNull(clonedFilter);
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final AreEqual filter = new AreEqual();

        // When
        final String json = new String(new JSONSerialiser().serialise(filter, true));

        // Then
        assertEquals("{\n" +
                "  \"class\" : \"gaffer.function.simple.filter.AreEqual\"\n" +
                "}", json);

        // When 2
        final AreEqual deserialisedFilter = new JSONSerialiser().deserialise(json.getBytes(), AreEqual.class);

        // Then 2
        assertNotNull(deserialisedFilter);
    }

    @Override
    protected Class<AreEqual> getFunctionClass() {
        return AreEqual.class;
    }

    @Override
    protected AreEqual getInstance() {
        return new AreEqual();
    }

    @Override
    protected Object[] getSomeAcceptedInput() {
        return new Object[0];
    }
}
