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
import gaffer.serialisation.Serialisation;

/**
 * RawFloatSerialiser serialises Floats into an IEEE floating point little-endian byte array.
 */
public class RawFloatSerialiser implements Serialisation {
    private static final long serialVersionUID = -8573401558869574875L;

    @Override
    public boolean canHandle(final Class clazz) {
        return Float.class.equals(clazz);
    }

    @Override
    public byte[] serialise(final Object o) throws SerialisationException {
        final byte[] out = new byte[4];
        final int value = Float.floatToRawIntBits((Float) o);
        out[0] = (byte) ((int) (value & 255));
        out[1] = (byte) ((value >> 8) & 255);
        out[2] = (byte) ((value >> 16) & 255);
        out[3] = (byte) ((value >> 24) & 255);
        return out;
    }

    @Override
    public Object deserialise(final byte[] bytes) throws SerialisationException {
        return Float.intBitsToFloat((int) ((int) bytes[0] & 255L
                | ((int) bytes[1] & 255L) << 8
                | ((int) bytes[2] & 255L) << 16
                | ((int) bytes[3] & 255L) << 24));
    }
}
