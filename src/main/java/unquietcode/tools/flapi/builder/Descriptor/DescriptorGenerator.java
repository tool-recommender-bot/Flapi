
package unquietcode.tools.flapi.builder.Descriptor;

import unquietcode.tools.flapi.builder.Descriptor.DescriptorBuilder.$;
import unquietcode.tools.flapi.support.BlockInvocationHandler;

import javax.annotation.Generated;


/**
 * This class was generated using Flapi, the fluent API generator for Java.
 * Modifications to this file will be lost upon regeneration.
 * You have been warned!
 * 
 * Visit https://github.com/UnquietCode/Flapi for more information.
 * 
 * 
 * Generated on July 01, 2013 20:13:15 PDT using version 0.4
 */
@Generated(value = "unquietcode.tools.flapi", date = "July 01, 2013 20:13:15 PDT", comments = "generated using Flapi, the fluent API generator for Java")
public class DescriptorGenerator {
    @SuppressWarnings("unchecked")
    public static $<Void> create(DescriptorHelper helper) {
        if (helper == null) {
            throw new IllegalArgumentException("Helper cannot be null.");
        }
         
        return new BlockInvocationHandler(helper, null)._proxy($.class);
    }
}
