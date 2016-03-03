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

package gaffer.function.processor;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import gaffer.function.AggregateFunction;
import gaffer.function.Tuple;
import gaffer.function.context.PassThroughFunctionContext;

import java.util.ArrayList;
import java.util.List;

/**
 * An <code>Aggregator</code> is a {@link gaffer.function.processor.Processor} that applies
 * {@link gaffer.function.AggregateFunction}s to a set of input {@link gaffer.function.Tuple}s, combining them to
 * produce a single output tuple. Typically, a client will group tuples together in some way and use an
 * <code>Aggregator</code> to combine them a group at a time, initialising the functions between each group.
 *
 * @param <R> The type of reference used by tuples.
 */
public class Aggregator<R> extends Processor<R, PassThroughFunctionContext<R, AggregateFunction>> {

    private boolean initialised = false;

    /**
     * Initialise the {@link gaffer.function.AggregateFunction}s used by this <code>Aggregator</code>.
     */
    public void initFunctions() {
        if (functions == null) {
            return;
        }
        safeInitFunctions();
    }

    /**
     * Aggregate an input {@link gaffer.function.Tuple} using {@link gaffer.function.AggregateFunction}s.
     *
     * @param tuple {@link gaffer.function.Tuple} to be aggregated.
     */
    public void aggregate(final Tuple<R> tuple) {
        if (functions == null) {
            return;
        }
        if (!initialised) {
            safeInitFunctions();
            initialised = true;
        }
        for (PassThroughFunctionContext<R, AggregateFunction> functionContext : functions) {
            Object[] selection = functionContext.select(tuple);
            if (selection != null) {
                functionContext.getFunction().aggregate(selection);
            }
        }
    }

    /**
     * Write the current aggregated results into an output {@link gaffer.function.Tuple}.
     *
     * @param tuple Output {@link gaffer.function.Tuple}.
     */
    public void state(final Tuple<R> tuple) {
        if (functions == null) {
            return;
        }
        for (PassThroughFunctionContext<R, AggregateFunction> functionContext : functions) {
            functionContext.project(tuple, functionContext.getFunction().state());
        }
    }

    /**
     * Create a deep copy of this <code>Aggregator</code>.
     *
     * @return Deep copy of this <code>Aggregator</code>.
     */
    @SuppressWarnings("CloneDoesntCallSuperClone")
    @SuppressFBWarnings(value = "CN_IDIOM_NO_SUPER_CALL", justification = "Does not required any fields from the parent class")
    @Override
    public Aggregator<R> clone() {
        final Aggregator<R> clone = new Aggregator<>();
        if (null != functions) {
            clone.addFunctions(cloneFunctions());
        }

        return clone;
    }

    /**
     * Create a deep copy of the {@link gaffer.function.context.PassThroughFunctionContext}s executed by this
     * <code>Aggregator</code>.
     *
     * @return Deep copy of {@link gaffer.function.context.PassThroughFunctionContext}s.
     */
    protected List<PassThroughFunctionContext<R, AggregateFunction>> cloneFunctions() {
        final List<PassThroughFunctionContext<R, AggregateFunction>> functionClones = new ArrayList<>();

        for (final PassThroughFunctionContext<R, AggregateFunction> function : functions) {
            final PassThroughFunctionContext<R, AggregateFunction> cloneContext = new PassThroughFunctionContext<>();
            cloneContext.setSelection(function.getSelection());

            final AggregateFunction af = function.getFunction();
            if (af != null) {
                cloneContext.setFunction(af.statelessClone());
            }
            functionClones.add(cloneContext);
        }

        return functionClones;
    }

    private void safeInitFunctions() {
        for (PassThroughFunctionContext<R, AggregateFunction> functionContext : functions) {
            functionContext.getFunction().init();
        }
    }

    /**
     * Implementation of the Builder pattern for {@link gaffer.function.processor.Aggregator}.
     *
     * @param <R> The type of reference used by tuples.
     */
    public static class Builder<R> {
        private final Aggregator<R> aggregator;
        private PassThroughFunctionContext.Builder<R, AggregateFunction> contextBuilder = new PassThroughFunctionContext.Builder<>();

        /**
         * Create a <code>Builder</code> to configure a new {@link gaffer.function.processor.Aggregator}.
         */
        public Builder() {
            this(new Aggregator<R>());
        }

        /**
         * Create a <code>Builder</code> to configure an {@link gaffer.function.processor.Aggregator}.
         *
         * @param aggregator {@link gaffer.function.processor.Aggregator} to be configured.
         */
        public Builder(final Aggregator<R> aggregator) {
            this.aggregator = aggregator;
        }

        /**
         * Set the selection references for the current {@link gaffer.function.context.PassThroughFunctionContext}. If
         * the current context already has a selection set, a new context is created and made current. Returns this
         * <code>Builder</code> for further configuration.
         *
         * @param selection Selection references.
         * @return This <code>Builder</code>.
         */
        public Builder<R> select(final R... selection) {
            if (contextBuilder.isSelected()) {
                buildContext();
            }

            contextBuilder.select(selection);
            return this;
        }

        /**
         * Set the {@link gaffer.function.AggregateFunction} to be executed by the current
         * {@link gaffer.function.context.PassThroughFunctionContext}. If the current context already has a function
         * set, a new context is created and made current. Returns this <code>Builder</code> for further configuration.
         *
         * @param function {@link gaffer.function.AggregateFunction} to be executed.
         * @return This <code>Builder</code>.
         */
        public Builder<R> execute(final AggregateFunction function) {
            if (contextBuilder.isExecuted()) {
                buildContext();
            }

            contextBuilder.execute(function);
            return this;
        }


        /**
         * Build the {@link gaffer.function.processor.Aggregator} configured by this <code>Builder</code>.
         *
         * @return Configured {@link gaffer.function.processor.Aggregator}.
         */
        public Aggregator<R> build() {
            buildContext();
            return aggregator;
        }

        /**
         * Build the current {@link gaffer.function.context.PassThroughFunctionContext}, add it to the
         * {@link gaffer.function.processor.Aggregator} and create a new current context.
         *
         * @return This <code>Builder</code>.
         */
        private Builder<R> buildContext() {
            if (contextBuilder.isSelected() || contextBuilder.isExecuted()) {
                aggregator.addFunction(contextBuilder.build());
                contextBuilder = new PassThroughFunctionContext.Builder<>();
            }

            return this;
        }
    }
}
