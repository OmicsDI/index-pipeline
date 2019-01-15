package uk.ac.ebi.ddi.pipeline.indexer.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author Yasset Perez-Riverol (ypriverol@gmail.com)
 * @date 18/08/2015
 */

@Data
public class Tuple<K, V> implements Serializable {
    private K key;
    private V value;

    public Tuple(K key, V value) {
        this.key = key;
        this.value = value;
    }
}
