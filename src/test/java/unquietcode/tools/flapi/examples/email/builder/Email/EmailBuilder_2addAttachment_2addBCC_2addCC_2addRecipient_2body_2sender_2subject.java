
package unquietcode.tools.flapi.examples.email.builder.Email;

import unquietcode.tools.flapi.examples.email.EmailMessage;
import unquietcode.tools.flapi.examples.email.builder.Email.EmailBuilder.Start;
import unquietcode.tools.flapi.runtime.MethodInfo;
import unquietcode.tools.flapi.runtime.Tracked;
import unquietcode.tools.flapi.runtime.TransitionType;

import javax.annotation.Generated;
import java.io.File;


/**
 * This class was generated using Flapi, the fluent API generator for Java.
 * Modifications to this file will be lost upon regeneration.
 * You have been warned!
 * 
 * Visit https://github.com/UnquietCode/Flapi for more information.
 * 
 * 
 * Generated on August 13, 2014 16:08:21 PDT using version 0.0-DEVELOPMENT
 * @see unquietcode.tools.flapi.examples.email.builder.Email.EmailHelper
 */
@Generated(value = "unquietcode.tools.flapi", date = "2014-08-13T16:08:21-07:00", comments = "generated using Flapi, the fluent API generator for Java")
public interface EmailBuilder_2addAttachment_2addBCC_2addCC_2addRecipient_2body_2sender_2subject<_ReturnType> {
    @MethodInfo(type = TransitionType.Recursive)
    Start<_ReturnType> addAttachment(File file);

    @MethodInfo(type = TransitionType.Recursive)
    Start<_ReturnType> addBCC(String emailAddress);

    @MethodInfo(type = TransitionType.Recursive)
    Start<_ReturnType> addCC(String emailAddress);

    @MethodInfo(type = TransitionType.Recursive)
    @Tracked(atLeast = 1, key = "addRecipient_1String_emailAddre")
    Start<_ReturnType> addRecipient(String emailAddress);

    @MethodInfo(type = TransitionType.Lateral)
    EmailBuilder_2addAttachment_2addBCC_2addCC_2addRecipient_2sender_2subject<_ReturnType> body(String text);

    @MethodInfo(type = TransitionType.Terminal)
    EmailMessage send();

    @MethodInfo(type = TransitionType.Lateral)
    @Tracked(atLeast = 1, key = "sender_1String_emailAddre")
    EmailBuilder_2addAttachment_2addBCC_2addCC_2addRecipient_2body_2subject<_ReturnType> sender(String emailAddress);

    @MethodInfo(type = TransitionType.Lateral)
    EmailBuilder_2addAttachment_2addBCC_2addCC_2addRecipient_2body_2sender<_ReturnType> subject(String subject);
}