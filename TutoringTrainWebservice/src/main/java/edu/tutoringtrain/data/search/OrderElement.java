/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.tutoringtrain.data.search;

/**
 *
 * @author Elias
 * @param <T>
 */
public class OrderElement<T extends EntityProp> {
    private T prop;
    private OrderDirection direction;

    public OrderElement() {
    }

    public OrderElement(T prop, OrderDirection direction) {
        this.prop = prop;
        this.direction = direction;
    }

    public T getProp() {
        return prop;
    }

    public void setProp(T prop) {
        this.prop = prop;
    }

    public OrderDirection getDirection() {
        return direction;
    }

    public void setDirection(OrderDirection direction) {
        this.direction = direction;
    }
}
