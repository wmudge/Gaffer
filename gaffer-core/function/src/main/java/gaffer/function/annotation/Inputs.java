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

package gaffer.function.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The <code>Inputs</code> annotation is used by {@link gaffer.function.Function} implementations to report accepted
 * argument types for it's execution method. It is used to enable runtime type checking to ensure that a function is
 * applicable to it's arguments.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Inputs {
    Class<?>[] value();
}
