package org.fruit.alayer;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;


/**
 * Tags are labels that can be attached to <code>Taggable</code> objects. They are similar to keys in <code>Map</code>'s.
 * They have a name and a type and are associated with values who must be of that type.
 */
public final class Tag<T> implements Serializable {
    private final static ConcurrentHashMap<Tag<?>, Tag<?>> existingTags = new ConcurrentHashMap<Tag<?>, Tag<?>>();

    /**
     * Returns a tag object which is identified by <code>name</code> and <code>valueType</code>.
     * @param name The name of the tag
     * @param valueType The type of the values that are associated with this tag.
     * @return A tag object.
     */
    @SuppressWarnings("unchecked")
    public static <T> Tag<T> from(String name, Class<T> valueType){
        // Assert.notNull(name, valueType);
        Tag<T> ret = new Tag<T>(name, valueType);
        Tag<T> existing = (Tag<T>)existingTags.putIfAbsent(ret, ret);
        if(existing != null)
            return existing;
        return ret;
    }

    private static final long serialVersionUID = -1215427100999751182L;
    private final Class<T> clazz;
    private final String name;
    private int hashcode;

    private Tag(String name, Class<T> clazz){
        this.clazz = clazz;
        this.name = name;
    }

    /**
     * The name of the tag
     * @return the name of the tag
     */
    public String name() { return name; }

    /**
     * The type of the values associated with this tag (e.g. <code>String</code>)
     * @return value type
     */
    public Class<T> type() { return clazz; }
    public String toString(){ return name; }

    public int hashCode(){
        int ret = hashcode;
        if(ret == 0){
            ret = name.hashCode() + 31 * hashCode(clazz.getCanonicalName());   //Class<T>.hashCode() is not stable across serializations!!
            hashcode = ret;
        }
        return hashcode;
    }

    public int hashCode(Object o) {
        return o == null ? 0 : o.hashCode();
    }

    public boolean equals(Object other){
        if(other == this)
            return true;
        if(other instanceof Tag){
            Tag<?> ot = (Tag<?>) other;
            return name.equals(ot.name) && clazz.equals(ot.clazz);
        }
        return false;
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException{
        ois.defaultReadObject();
    }

    private Object readResolve() throws ObjectStreamException {
        Tag<?> existing = existingTags.putIfAbsent(this, this);
        return existing == null ? this : existing;
    }

    // by urueda
    public boolean isOneOf(Tag<?>... oneOf){
        for(Tag<?> o : oneOf){
            if(this.equals(o))
                return true;
        }
        return false;
    }
}