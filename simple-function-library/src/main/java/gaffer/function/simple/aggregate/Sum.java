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
package gaffer.function.simple.aggregate;

import gaffer.function.annotation.Inputs;
import gaffer.function.annotation.Outputs;

/**
 * An <code>Min</code> is a {@link gaffer.function.SimpleAggregateFunction} that takes in
 * {@link java.lang.Number}s of the same type and calculates the sum.
 * If you know the type of number that will be used then this can be set by calling setMode(NumberType),
 * otherwise it will be automatically set for you using the class of the first number passed in.
 *
 * @see gaffer.function.simple.aggregate.NumericAggregateFunction
 */
@Inputs(Number.class)
@Outputs(Number.class)
public class Sum extends NumericAggregateFunction {
    @Override
    protected void initInt() {
        aggregate = 0;
    }

    @Override
    protected void initLong() {
        aggregate = 0L;
    }

    @Override
    protected void initDouble() {
        aggregate = 0.0d;
    }

    @Override
    protected void aggregateInt(final Integer input) {
        aggregate = (Integer) aggregate + input;
    }

    @Override
    protected void aggregateLong(final Long input) {
        aggregate = (Long) aggregate + input;
    }

    @Override
    protected void aggregateDouble(final Double input) {
        aggregate = (Double) aggregate + input;
    }

    public Sum statelessClone() {
        Sum sum = new Sum();
        sum.setMode(super.getMode());
        sum.init();
        return sum;
    }
}
