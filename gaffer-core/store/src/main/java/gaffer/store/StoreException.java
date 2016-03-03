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

package gaffer.store;

/**
 * A <code>StoreException</code> is a fairly generic exception for a wide range of issues occurred in a
 * {@link gaffer.store.Store}.
 */
public class StoreException extends Exception {
    private static final long serialVersionUID = -4128226949832820609L;

    public StoreException(final Throwable e) {
        super(e);
    }

    public StoreException(final String message, final Throwable e) {
        super(message, e);
    }

    public StoreException(final String message) {
        super(message);
    }
}
