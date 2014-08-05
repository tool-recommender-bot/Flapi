/*********************************************************************
 Flapi, the fluent API builder for Java.
 Visit the project page at https://github.com/UnquietCode/Flapi

 Flapi is free and open software provided without a license.
 ********************************************************************/

package unquietcode.tools.flapi.helpers;

import unquietcode.tools.flapi.builder.Block.BlockHelper;
import unquietcode.tools.flapi.builder.Descriptor.DescriptorHelper;
import unquietcode.tools.flapi.builder.Method.MethodHelper;
import unquietcode.tools.flapi.outline.DescriptorOutline;

import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ben Fagin
 * @version 03-10-2012
 */
public class DescriptorHelperImpl extends DescriptorConfiguratorHelperImpl implements DescriptorHelper {

	public DescriptorHelperImpl() {
		super(new DescriptorOutline());
	}

	@Override
	public void setDescriptorName(String descriptorName) {
		if (descriptorName == null || descriptorName.trim().isEmpty()) {
			throw new IllegalArgumentException("Name cannot be empty.");
		}
		outline.setName(descriptorName);
	}

	@Override
	public void setReturnType(Class returnType) {
		if (returnType == null) {
			throw new IllegalArgumentException("Return type cannot be null.");
		}
		setReturnType(returnType.getName());
	}

	@Override
	public void setReturnType(String returnType) {
		if (returnType == null) {
			throw new IllegalArgumentException("Return type cannot be null.");
		}
		outline.setReturnType(returnType);
	}

	@Override
	public void addBlockReference(String blockName, String methodSignature, AtomicReference<MethodHelper> _helper1) {
		BlockHelperImpl._addBlockReference(outline, blockName, methodSignature, _helper1);
	}

	@Override
	public void addMethod(String methodSignature, AtomicReference<MethodHelper> _helper1) {
		BlockHelperImpl._addMethod(outline, methodSignature, _helper1);
	}

	@Override
	public void startBlock(String blockName, String methodSignature, AtomicReference<MethodHelper> _helper1, AtomicReference<BlockHelper> _helper2) {
		BlockHelperImpl._startBlock(outline, blockName, methodSignature, _helper1, _helper2);
	}

	@Override
	public void startBlock(String methodSignature, AtomicReference<MethodHelper> _helper1, AtomicReference<BlockHelper> _helper2) {
		BlockHelperImpl._startBlock(outline, null, methodSignature, _helper1, _helper2);
	}

	@Override
	public void addEnumSelector(Class clazz, String methodSignature, AtomicReference<MethodHelper> _helper1) {
		BlockHelperImpl._addEnumSelector(outline, clazz, methodSignature, _helper1);
	}
}