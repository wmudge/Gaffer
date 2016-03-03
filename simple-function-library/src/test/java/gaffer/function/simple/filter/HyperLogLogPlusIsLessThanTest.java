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

import com.clearspring.analytics.stream.cardinality.HyperLogLogPlus;
import gaffer.exception.SerialisationException;
import gaffer.function.FilterFunction;
import gaffer.function.FilterFunctionTest;
import gaffer.function.Function;
import gaffer.jsonserialisation.JSONSerialiser;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

public class HyperLogLogPlusIsLessThanTest extends FilterFunctionTest {

    private static HyperLogLogPlus hyperLogLogPlusWithCardinality5;
    private static HyperLogLogPlus hyperLogLogPlusWithCardinality15;
    private static HyperLogLogPlus hyperLogLogPlusWithCardinality31;

    @Before
    public void setup() {
        hyperLogLogPlusWithCardinality5 = new HyperLogLogPlus(5, 5);
        for (int i = 1; i <= 5; i++) {
            hyperLogLogPlusWithCardinality5.offer(i);
        }
        assertEquals(5l, hyperLogLogPlusWithCardinality5.cardinality());

        hyperLogLogPlusWithCardinality15 = new HyperLogLogPlus(5, 5);
        for (int i = 1; i <= 18; i++) {
            hyperLogLogPlusWithCardinality15.offer(i);
        }
        assertEquals(15l, hyperLogLogPlusWithCardinality15.cardinality());

        hyperLogLogPlusWithCardinality31 = new HyperLogLogPlus(5, 5);
        for (int i = 1; i <= 32; i++) {
            hyperLogLogPlusWithCardinality31.offer(i);
        }
        assertEquals(31l, hyperLogLogPlusWithCardinality31.cardinality());
    }

    @Test
    public void shouldAcceptWhenLessThan() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter._isValid(hyperLogLogPlusWithCardinality5);
        // Then
        assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenEqualToAndEqualToIsFalse() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter._isValid(hyperLogLogPlusWithCardinality15);
        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldAcceptWhenEqualToAndEqualToIsTrue() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15, true);
        // When
        boolean accepted = filter._isValid(hyperLogLogPlusWithCardinality15);
        // Then
        assertTrue(accepted);
    }

    @Test
    public void shouldRejectWhenMoreThan() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter._isValid(hyperLogLogPlusWithCardinality31);
        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldRejectWhenInputIsNull() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        boolean accepted = filter._isValid(null);
        // Then
        assertFalse(accepted);
    }

    @Test
    public void shouldCloneWithDefaultOrEqualTo() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15);
        // When
        HyperLogLogPlusIsLessThan clone = filter.statelessClone();
        // Then
        assertNotSame(filter, clone);
        assertEquals(15, clone.getControlValue());
        assertFalse(clone.getOrEqualTo());
    }

    @Test
    public void shouldCloneWithOrEqualToSetToTrue() {
        // Given
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(15, true);
        // When
        HyperLogLogPlusIsLessThan clone = filter.statelessClone();
        // Then
        assertNotSame(filter, clone);
        assertEquals(15, clone.getControlValue());
        assertTrue(clone.getOrEqualTo());
    }

    @Test
    public void shouldJsonSerialiseAndDeserialise() throws SerialisationException {
        // Given
        final long controlValue = 15;
        final HyperLogLogPlusIsLessThan filter = new HyperLogLogPlusIsLessThan(controlValue);

        // When 1
        final String json = new String(new JSONSerialiser().serialise(filter, true));
        // Then 1
        assertEquals("{\n" +
                "  \"class\" : \"gaffer.function.simple.filter.HyperLogLogPlusIsLessThan\",\n" +
                "  \"value\" : 15\n" +
                "}", json);

        // When 2
        final HyperLogLogPlusIsLessThan deserialisedProperty = new JSONSerialiser().deserialise(json.getBytes(), HyperLogLogPlusIsLessThan.class);
        // Then 2
        assertNotNull(deserialisedProperty);
        assertEquals(controlValue, deserialisedProperty.getControlValue());
    }

    @Override
    protected Class<? extends Function> getFunctionClass() {
        return HyperLogLogPlusIsLessThan.class;
    }

    @Override
    protected FilterFunction getInstance() {
        return new HyperLogLogPlusIsLessThan(10);
    }

    @Override
    protected Object[] getSomeAcceptedInput() {
        return new Object[]{hyperLogLogPlusWithCardinality5};
    }
}
