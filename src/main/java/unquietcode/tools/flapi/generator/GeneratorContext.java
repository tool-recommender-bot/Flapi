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

package unquietcode.tools.flapi.generator;

import com.sun.codemodel.*;
import unquietcode.tools.flapi.*;
import unquietcode.tools.flapi.generator.naming.DefaultNameGenerator;
import unquietcode.tools.flapi.generator.naming.NameGenerator;
import unquietcode.tools.flapi.graph.BlockMethodTracker;
import unquietcode.tools.flapi.graph.components.StateClass;
import unquietcode.tools.flapi.graph.components.Transition;
import unquietcode.tools.flapi.java.JavaType;
import unquietcode.tools.flapi.java.MethodSignature;
import unquietcode.tools.flapi.runtime.TransitionType;

import javax.annotation.Generated;
import javax.lang.model.SourceVersion;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author Ben Fagin
 * @version 03-07-2012
 */
public class GeneratorContext {
	private static final SimpleDateFormat humanReadableDateFormat = new SimpleDateFormat("MMMM dd, yyyy H:mm:ss z");

	// JDK 6 doesn't have a concept of 'X', so it is omitted in those cases
	// (Probably it is time to dump support for Java 6.)
	private static final SimpleDateFormat iso8601DateFormat; static {
		boolean legacyVersion =
			SourceVersion.latestSupported() == SourceVersion.RELEASE_5
		 || SourceVersion.latestSupported() == SourceVersion.RELEASE_6
		;

		iso8601DateFormat = legacyVersion
						  ? new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
						  : new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX")
		;
	}

	public final JCodeModel model = new JCodeModel();
	public final BlockMethodTracker helperMethods = new BlockMethodTracker();

	private final JPackage thePackage;
	private final Map<String, JDefinedClass> interfaces = new HashMap<String, JDefinedClass>();
	private final Map<String, JDefinedClass> classes = new HashMap<String, JDefinedClass>();
	private NameGenerator nameGenerator = new DefaultNameGenerator();
	private boolean enableTimestamps = true;

	public GeneratorContext(String rootPackage) {
		if (rootPackage != null && !rootPackage.trim().equals("")) {
			thePackage = model._package(checkPackageName(rootPackage));
		} else {
			thePackage = model.rootPackage();
		}
	}

	private String checkPackageName(String name) {
		name = name.trim();

		if (!SourceVersion.isName(name)) {
			throw new DescriptorBuilderException("Package name '"+name+"' is not allowed.");
		}

		return name;
	}

	public NameGenerator getNameGenerator() {
		return nameGenerator;
	}

	public void setNameGenerator(NameGenerator generator) {
		this.nameGenerator = Objects.requireNonNull(generator);
	}

	public void disableTimestamps(boolean value) {
		enableTimestamps = !value;
	}

	public Pair<JDefinedClass, Boolean> getOrCreateInterface(String subPackage, String name) {
		JDefinedClass _interface = interfaces.get(name);
		final boolean created = _interface == null;

		if (_interface == null) {
			JPackage _package = subPackage != null && !subPackage.isEmpty()
							  ? thePackage.subPackage(subPackage)
							  : thePackage;
			try {
				_interface = _package._interface(name);
			} catch (JClassAlreadyExistsException ex) {
				throw new DescriptorBuilderException(ex);
			}

			interfaces.put(name, _interface);
			addGeneratedHeader(_interface);
		}

		return new Pair<JDefinedClass, Boolean>(_interface, created);
	}

	public Pair<JDefinedClass, Boolean> getOrCreateClass(String subPackage, String name) {
		JDefinedClass _class = classes.get(name);
		final boolean created = _class == null;

		if (_class == null) {
			JPackage _package = subPackage != null && !subPackage.isEmpty()
						  	  ? thePackage.subPackage(subPackage)
							  : thePackage;
			try {
				_class = _package._class(JMod.PUBLIC, name);
				classes.put(name, _class);
				addGeneratedHeader(_class);
			} catch (JClassAlreadyExistsException ex) {
				throw new DescriptorBuilderException(ex);
			}
		}

		return new Pair<JDefinedClass, Boolean>(_class, created);
	}

	public boolean doesClassExist(String name) {
		return classes.containsKey(name);
	}

	public boolean doesInterfaceExist(String name) {
		return interfaces.containsKey(name);
	}

	//---o---o---o---o---o---o---o--- Name Creation --o---o---o---o---o---o---o---o---o---o---//

	private final Map<String, String> hashToSuffix = new HashMap<String, String>();
	private final Map<String, CharacterGenerator> nameToGenerator = new HashMap<String, CharacterGenerator>();

	/**
	 * Generates a name based on the following rules:
	 *  once → prefix + state name + suffix
	 *  1st method → name$number
	 *  2nd..Nth method → name$letter$number
	 *
	 *  Where 'letter' is for instance id and number is maxOccurs.
	 *  The letter was added to address FLAPI-105, which identified
	 *  a lack of support for two methods with the same name but
	 *  different parameters.
	 *
	 * @param state state to generate a name for
	 * @return the generated name, which should be unique across the graph
	 */
	public String getGeneratedName(StateClass state) {
		final String builderName = nameGenerator.builderName(state.getName());
		final StringBuilder name = new StringBuilder(builderName);

		for (Transition transition : state.getTransitions()) {
			final boolean isImplicit = transition.info().isImplicit();

			// reduce noise by not utilizing all of the available names
			if (transition.getType() == TransitionType.Terminal && !isImplicit) {
				continue;
			}

			// also skip Ascending when it's leading nowhere
			if (transition.getType() == TransitionType.Ascending && transition.getOwner().isTopLevel() && !isImplicit) {
				continue;
			}

			MethodSignature signature = transition.getMethodSignature();
			String methodName = signature.methodName;

			// create the special method+parameter 'hash' key
			String krazyKey = methodName;
			for (Pair<JavaType, String> param : signature.params) {
				krazyKey += "|"+param.first.typeName;
			}

			// if this specific combo has been seen before, use the existing char
			if (hashToSuffix.containsKey(krazyKey)) {
				methodName += hashToSuffix.get(krazyKey);

			// else create a new suffix
			} else {
				CharacterGenerator characterGen;
				String methodSuffix;

				if (nameToGenerator.containsKey(methodName)) {
					characterGen = nameToGenerator.get(methodName);
					methodSuffix = characterGen.getAndIncrement();
				} else {
					characterGen = new CharacterGenerator("_1");
					nameToGenerator.put(methodName, characterGen);
					methodSuffix = "";  // don't label the first one because generally it's the only one
				}

				methodName += methodSuffix;
				hashToSuffix.put(krazyKey, methodSuffix);
			}

			// method name
			name.append("_2").append(nameGenerator.methodName(methodName));

			// remaining invocations
			if (transition.info().getMaxOccurrences() > 1) {
				name.append("_3").append(transition.info().getMaxOccurrences());
			}

			// triggered methods
			if (transition.info().didTrigger()) {
				name.append("_4t");
			}

			// implicit terminals
			if (isImplicit) {
				name.append("_5t");
			}
		}

		return nameGenerator.className(name.toString());
	}

	//---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---o---//

	private String header = null;
	private Date generationDate = null;

	private void addGeneratedHeader(JDefinedClass clazz) {
		if (header == null) {
			generationDate = new Date();

			StringBuilder sb = new StringBuilder()
				.append("This class was generated using Flapi, the fluent API generator for Java.\n")
				.append("Modifications to this file will be lost upon regeneration.\n")
				.append("You have been warned!\n")
				.append("\n")
				.append("Visit ").append(Constants.PROJECT_URL).append(" for more information.\n")
				.append("\n\n")
			;

			// optional timestamp with project version
			if (enableTimestamps) {
				sb.append("Generated on ").append(humanReadableDateFormat.format(generationDate))
				  .append(" using version ").append(Constants.PROJECT_VERSION);
			}

			// otherwise just the project version
			else {
				sb.append("Generated using version ").append(Constants.PROJECT_VERSION);
			}

			header = sb.toString();
		}

		// javadoc header
		clazz.javadoc().append(header);

		// @Generated, when JDK version is >= 6
		if (Flapi.getJDKVersion().ordinal() >= SourceVersion.RELEASE_6.ordinal()) {
			final JAnnotationUse generatedAnnotation = clazz.annotate(Generated.class);
			generatedAnnotation.param("value", "unquietcode.tools.flapi");

			// optional date
			if (enableTimestamps) {
				generatedAnnotation.param("date", iso8601DateFormat.format(generationDate));
			}

			// always a comment with tool version
			generatedAnnotation.param("comments", "generated using Flapi, the fluent API generator for Java, version "+Constants.PROJECT_VERSION);
		}
	}
}

// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: //
// :::::::::::::::::::::::::: P07470 :::::::::::::::::::::::::::::::: //
// :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::: //