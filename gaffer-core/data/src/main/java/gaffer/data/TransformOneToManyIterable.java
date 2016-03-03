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

package gaffer.data;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A <code>TransformToMultiIterable</code> allows {@link Iterable}s to be lazily validated and transformed without
 * loading the entire iterable into memory. The easiest way to use this class is to create an anonymous inner class.
 * This class is very similar to {@link gaffer.data.TransformOneToManyIterable} except that this class transforms one to many
 * items.
 *
 * @param <INPUT>  The input iterable type.
 * @param <OUTPUT> the output iterable type.
 */
public abstract class TransformOneToManyIterable<INPUT, OUTPUT> implements Iterable<OUTPUT> {
    private final Iterable<INPUT> input;
    private final Validator<INPUT> validator;
    private final boolean skipInvalid;

    /**
     * Constructs an <code>TransformOneToManyIterable</code> with the given input {@link Iterable} and no validation.
     *
     * @param input the input {@link Iterable}
     */
    public TransformOneToManyIterable(final Iterable<INPUT> input) {
        this(input, new AlwaysValid<INPUT>(), false);
    }

    /**
     * Constructs an <code>TransformOneToManyIterable</code> with the given input {@link Iterable} and
     * {@link Validator}. Invalid items will throw an {@link IllegalArgumentException} to be thrown.
     *
     * @param input     the input {@link Iterable}
     * @param validator the {@link Validator}
     */
    public TransformOneToManyIterable(final Iterable<INPUT> input, final Validator<INPUT> validator) {
        this(input, validator, false);
    }

    /**
     * Constructs an <code>TransformOneToManyIterable</code> with the given input {@link Iterable},
     * {@link Validator} and a skipInvalid flag to determine whether invalid items should be skipped.
     *
     * @param input       the input {@link Iterable}
     * @param validator   the {@link Validator}
     * @param skipInvalid if true invalid items should be skipped
     */
    public TransformOneToManyIterable(final Iterable<INPUT> input, final Validator<INPUT> validator, final boolean skipInvalid) {
        this.input = input;
        this.validator = validator;
        this.skipInvalid = skipInvalid;
    }

    /**
     * @return an {@link java.util.Iterator} that lazy transforms the INPUT items to OUTPUT items
     */
    public Iterator<OUTPUT> iterator() {
        return new Iterator<OUTPUT>() {
            private final Iterator<INPUT> inputItr = input.iterator();

            private Iterator<OUTPUT> nextElements;
            private Boolean hasNext;

            @Override
            public boolean hasNext() {
                if (null == hasNext) {
                    if (inputItr.hasNext()) {
                        final INPUT possibleNext = inputItr.next();
                        if (validator.validate(possibleNext)) {
                            final Iterable<OUTPUT> nextElementsIterable = transform(possibleNext);
                            if (null != nextElementsIterable) {
                                nextElements = nextElementsIterable.iterator();
                                hasNext = true;
                            } else {
                                hasNext();
                            }
                        } else if (skipInvalid) {
                            hasNext();
                        } else {
                            handleInvalidItem(possibleNext);
                        }
                    } else {
                        hasNext = false;
                        nextElements = null;
                    }
                }

                return _hasNext();
            }

            @Override
            public OUTPUT next() {
                if (!_hasNext()) {
                    if (!hasNext()) {
                        throw new NoSuchElementException("Reached the end of the iterator");
                    }
                }

                final OUTPUT elementToReturn = nextElements.next();
                if (!nextElements.hasNext()) {
                    nextElements = null;
                    hasNext = null;
                }

                return elementToReturn;
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException("Cannot call remove on a " + getIterableClass().getSimpleName() + " iterator");
            }

            private boolean _hasNext() {
                return Boolean.TRUE.equals(hasNext) && null != nextElements && nextElements.hasNext();
            }
        };
    }

    /**
     * Transforms the INPUT item into an OUTPUT iterable.
     *
     * @param item the INPUT item to be transformed
     * @return the transformed OUTPUT iterable
     */
    protected abstract Iterable<OUTPUT> transform(final INPUT item);

    /**
     * Handles an invalid item. Simply throws an {@link IllegalArgumentException} explaining that the item is
     * invalid. Override this method to handle invalid items differently.
     *
     * @param item the invalid INPUT item
     * @throws IllegalArgumentException always thrown unless this method is overridden.
     */
    protected void handleInvalidItem(final INPUT item) throws IllegalArgumentException {
        final String itemDescription = null != item ? item.toString() : "<unknown>";
        throw new IllegalArgumentException("Next " + itemDescription + " in iterable is not valid.");
    }

    private Class<? extends TransformOneToManyIterable> getIterableClass() {
        return getClass();
    }
}
