/**
 * Copyright (c) 2012-2018 BITPlan GmbH
 *
 * http://www.bitplan.com
 *
 * This file is part of the Opensource project at:
 * https://github.com/BITPlan/com.bitplan.rest.freemarker
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
package com.bitplan.rest.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.FileTemplateLoader;
import freemarker.cache.MultiTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.ext.beans.BeansWrapper;
import freemarker.template.Template;

/**
 * supply a free marker configuration
 * @author wf
 *
 */
public class FreeMarkerConfiguration {
	
	protected static freemarker.template.Configuration cfg = null;

	// multiple template pathes for files possible
	protected static List<String> templatePaths=new ArrayList<String>();
	
	// list of classes to load templates from
	protected static List<TemplateClass> templateClasses=new ArrayList<TemplateClass>();
	
	/**
	 * add a path to the template path
	 * @param path - the path to add 
	 */
	public static void addTemplatePath(String path) {
		templatePaths.add(path);
		// force configuration refresh
		cfg=null;
	}
	
	/**
	 * add the given template class from the given clazz
	 * @param clazz the Class to add
	 * @param path the path to use
	 */
	@SuppressWarnings("rawtypes")
	public static void addTemplateClass(Class clazz, String path) {
		TemplateClass tClass=new TemplateClass();
		tClass.clazz=clazz;
		tClass.path=path;
		templateClasses.add(tClass);
		// force configuration refresh
		cfg=null;
	}
	
	/**
	 * get the freemarker configuration
	 * @return the configuration
	 * @throws IOException if an I/O problem occurs
	 */
	public static freemarker.template.Configuration getFreemarkerConfiguration() throws IOException {
		if (cfg == null) {
			// http://freemarker.sourceforge.net/docs/pgui_quickstart_createconfiguration.html
			cfg = new freemarker.template.Configuration();
			// Specify the data source where the template files come from.
			// use multiple sources
			List<TemplateLoader> loaderList=new ArrayList<TemplateLoader>();
			for (String templatePath:templatePaths) {
				loaderList.add(new FileTemplateLoader(new File(templatePath)));
			}
			for (TemplateClass templateClass:templateClasses) {
				loaderList.add(new ClassTemplateLoader(templateClass.clazz,templateClass.path));
			}
			TemplateLoader[] loaders = new TemplateLoader[loaderList.size()];
			loaderList.toArray(loaders);
			MultiTemplateLoader mtl = new MultiTemplateLoader(loaders);

			cfg.setTemplateLoader(mtl);  
			
			// Specify how templates will see the data-model. This is an advanced
			// http://freemarker.sourceforge.net/docs/api/freemarker/ext/beans/BeansWrapper.html
			BeansWrapper wrapper = new BeansWrapper();
			wrapper.setSimpleMapWrapper(true);
			cfg.setObjectWrapper(wrapper);
			// http://freemarker.org/docs/app_faq.html#faq_number_grouping
			cfg.setNumberFormat("0.######"); 
		}
		return cfg;
	}

	/**
	 * get the Template with the given name
	 * @param templateName - the name of the template to look for
	 * @return - the matching Template
	 * @throws IOException  - in case of an I/O problem
	 */
	public static Template getTemplate(String templateName) throws IOException {
		Template result = getFreemarkerConfiguration().getTemplate(templateName);
		return result;
	}

	/**
	 * process the given template with the given map
	 * @param templateName - the name of the template to use
	 * @param rootMap - the map to pass on to Freemarker's template engine
	 * @return a String with the result of processing the template
	 * @throws Exception - in case of failure
	 */
	public static String doProcessTemplate(String templateName,Map<String,Object> rootMap) throws Exception {
		Template template=FreeMarkerConfiguration.getTemplate(templateName);
		StringWriter htmlWriter = new StringWriter();
		template.process(rootMap,htmlWriter);
		return htmlWriter.toString();
	}
	
	/**
	 * dummy main routine - will print out hint that this is a library and exit
	 * with error code 1
	 * 
	 * @param args - the command line arguments
	 */
	public static void main(String[] args) {
	  System.err.println("This is a Freemarker utility Library.");
	  System.err.println("It's available at https://github.com/BITPlan/com.bitplan.rest.freemarker");
	  System.err.println("There is no commandline support at this time");
	  System.exit(1);
	}
}
