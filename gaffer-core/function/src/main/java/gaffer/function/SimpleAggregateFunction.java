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

package gaffer.function;

/**
 * An <code>SimpleAggregateFunction</code> is an {@link AggregateFunction}
 * that takes a single input and returns a single output when state is called.
 *
 * @param <T> the type of input and output
 */
public abstract class SimpleAggregateFunction<T> extends AggregateFunction {
    @Override
    public void aggregate(final Object[] input) {
        if (null == input || 1 != input.length) {
            throw new IllegalArgumentException("Expected an input array of length 1");
        }

        try {
            _aggregate((T) input[0]);
        } catch (final ClassCastException e) {
            throw new IllegalArgumentException("Input does not match parametrised type");
        }
    }

    @Override
    public Object[] state() {
        return new Object[]{_state()};
    }

    protected abstract void _aggregate(final T input);

    protected abstract T _state();
}
