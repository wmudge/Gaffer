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
package gaffer.serialisation.simple.raw;

import gaffer.exception.SerialisationException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RawLongSerialiserTest {

    private static final RawLongSerialiser SERIALISER = new RawLongSerialiser();

    @Test
    public void testCanSerialiseASampleRange() throws SerialisationException {
        for (long i = 0; i < 1000; i++) {
            byte[] b = SERIALISER.serialise(i);
            Object o = SERIALISER.deserialise(b);
            assertEquals(Long.class, o.getClass());
            assertEquals(i, o);
        }
    }

    @Test
    public void canSerialiseLongMinValue() throws SerialisationException {
        byte[] b = SERIALISER.serialise(Long.MIN_VALUE);
        Object o = SERIALISER.deserialise(b);
        assertEquals(Long.class, o.getClass());
        assertEquals(Long.MIN_VALUE, o);
    }

    @Test
    public void canSerialiseLongMaxValue() throws SerialisationException {
        byte[] b = SERIALISER.serialise(Long.MAX_VALUE);
        Object o = SERIALISER.deserialise(b);
        assertEquals(Long.class, o.getClass());
        assertEquals(Long.MAX_VALUE, o);
    }

    @Test
    public void cantSerialiseStringClass() throws SerialisationException {
        assertFalse(SERIALISER.canHandle(String.class));
    }

    @Test
    public void canSerialiseLongClass() throws SerialisationException {
        assertTrue(SERIALISER.canHandle(Long.class));
    }
}