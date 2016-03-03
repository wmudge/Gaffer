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
package gaffer.integration.domain;

/**
 * Please note that this object has been created in order to test the ElementGenerator code in the Gaffer framework.
 *
 * It is not intended to be a representative example of how to map a domain object to a Gaffer graph element.  For an
 * example of how this mapping may be achieved, please see the 'example' project.
 */
public class EdgeDomainObject {

    private String source;
    private String destination;
    private Boolean directed;
    private Integer intProperty;
    private Long count;

    public EdgeDomainObject(final String source, final String destination, final Boolean directed, final Integer intProperty) {
        this.source = source;
        this.destination = destination;
        this.directed = directed;
        this.intProperty = intProperty;
    }

    public EdgeDomainObject() {
    }

    public Integer getIntProperty() {
        return intProperty;
    }

    public void setIntProperty(final Integer intProperty) {
        this.intProperty = intProperty;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(final Long count) {
        this.count = count;
    }

    public String getSource() {
        return source;
    }

    public void setSource(final String source) {
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(final String destination) {
        this.destination = destination;
    }

    public Boolean getDirected() {
        return directed;
    }

    public void setDirected(final Boolean directed) {
        this.directed = directed;
    }
}
