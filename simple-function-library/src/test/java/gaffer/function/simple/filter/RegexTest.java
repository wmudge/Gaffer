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
import gaffer.function.FilterFunction;
import gaffer.function.FilterFunctionTest;
import gaffer.function.Function;
import gaffer.jsonserialisation.JSONSerialiser;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class RegexTest extends FilterFunctionTest {
    @Test
    public void shouldAccepValidValue() {
        // Given
        final Regex filter = new Regex("te[a-d]{3}st");


        // When
        boolean accepted = filter._isValid("teaadst");

        // Then
        assertTrue(accepted);
    }

    @Test
    public void shouldRejectInvalidValue() {
        // Given
        final Regex filter = new Regex("fa[a-d]{3}il");

        // When
        boolean accepted = filter._isValid("favcdil");

        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldClone() {
        // Given
        final Regex filter = new Regex();

        // When
        final Regex clonedFilter = filter.statelessClone();

        // Then
        assertNotSame(filter, clonedFilter);
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final Regex filter = new Regex("test");

        // When
        final String json = new String(new JSONSerialiser().serialise(filter, true));

        // Then
        assertEquals("{\n" +
                "  \"class\" : \"gaffer.function.simple.filter.Regex\",\n" +
                "  \"value\" : {\n"
                + "    \"java.util.regex.Pattern\" : \"test\"\n"
                + "  }\n" +
                "}", json);

        // When 2
        final Regex deserialisedFilter = new JSONSerialiser().deserialise(json.getBytes(), Regex.class);

        // Then 2
        assertEquals(filter.getControlValue().pattern(), deserialisedFilter.getControlValue().pattern());
        assertNotNull(deserialisedFilter);
    }

    @Override
    protected FilterFunction getInstance() {
        return new Regex("[a-zA-Z]{1,12}");
    }

    @Override
    protected Object[] getSomeAcceptedInput() {
        return new Object[]{"suCCeSs"};
    }

    @Override
    protected Class<? extends Function> getFunctionClass() {
        return Regex.class;
    }

}