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

import gaffer.data.element.Edge;
import gaffer.data.element.Element;

/**
 * A <code>IsEdgeValidator</code> is a simple {@link gaffer.data.Validator} to validate if an
 * {@link gaffer.data.element.Element} is an instance of an {@link gaffer.data.element.Edge}.
 */
public class IsEdgeValidator implements Validator<Element> {
    /**
     * @param element the {@link gaffer.data.element.Element} to validate
     * @return true if the {@link gaffer.data.element.Element} is an instance of an {@link gaffer.data.element.Edge}.
     */
    @Override
    public boolean validate(final Element element) {
        return element instanceof Edge;
    }
}
