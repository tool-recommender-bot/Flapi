/*********************************************************************
 Copyright 2014 the Flapi authors

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 ********************************************************************/

package unquietcode.tools.flapi.helpers;

import unquietcode.tools.flapi.builder.Block.BlockHelper;
import unquietcode.tools.flapi.builder.BlockChain.BlockChainHelper;
import unquietcode.tools.flapi.outline.BlockOutline;
import unquietcode.tools.flapi.outline.BlockReference;
import unquietcode.tools.flapi.outline.MethodOutline;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Ben Fagin
 * @version 04-21-2012
 */
public class BlockChainHelperImpl implements BlockChainHelper {
	private final MethodOutline blockMethod;
	private final List<BlockOutline> chain = new ArrayList<BlockOutline>();

	BlockChainHelperImpl(MethodOutline blockMethod) {
		this.blockMethod = blockMethod;
	}

	/*
		add the reference to the chain (to be resolved later)
	*/
	@Override
	public void addBlockReference(String blockName) {
		BlockReference ref = new BlockReference(blockName);
		ref.setConstructor(blockMethod);
		chain.add(ref);
	}

	@Override
	public void startBlock(String blockName, AtomicReference<BlockHelper> _helper1) {
		BlockOutline innerBlock = new BlockOutline();
		innerBlock.setName(blockName);
		chain.add(innerBlock);

		_helper1.set(new BlockHelperImpl(innerBlock));
	}

	@Override
	public void startBlock(AtomicReference<BlockHelper> _helper1) {
		BlockOutline anonymousBlock = new BlockOutline();
		chain.add(anonymousBlock);

		_helper1.set(new BlockHelperImpl(anonymousBlock));
	}

	@Override
	public void end() {
		blockMethod.getBlockChain().addAll(0, chain);
	}
}
