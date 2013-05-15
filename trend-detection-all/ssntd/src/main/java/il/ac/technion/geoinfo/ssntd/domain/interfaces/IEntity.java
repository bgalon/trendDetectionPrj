package il.ac.technion.geoinfo.ssntd.domain.interfaces;

/**
 * Created with IntelliJ IDEA.
 * User: Ben
 * Date: 30/04/13
 * Time: 19:18
 * To change this template use File | Settings | File Templates.
 */
public interface IEntity {

    public boolean hasProperty( String key );

    public Object getProperty( String key );

    public void setProperty( String key, Object value );

}
