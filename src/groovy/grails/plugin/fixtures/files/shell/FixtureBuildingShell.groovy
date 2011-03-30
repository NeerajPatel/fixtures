/*
 * Copyright 2010 Grails Plugin Collective
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
package grails.plugin.fixtures.files.shell

import grails.plugin.fixtures.files.shell.handler.*

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

import org.springframework.core.io.Resource 


class FixtureBuildingShell extends GroovyShell {

	private static final Log log = LogFactory.getLog(FixtureBuildingShell)
	private static final String LOG_PREFIX = 'grails.app.'

	static handlers = [
		FixtureHandler, RequireHandler, RequireDefinitionsHandler, 
		RequireBeansHandler, PreHandler, PostHandler, IncludeHandler,
		LoadHandler, BuildHandler
	]
	
	FixtureBuildingShell(fileLoader) {
		super(fileLoader.fixture.grailsApplication.classLoader)
		handlers*.newInstance(fileLoader)*.register(this)
	}

	def evaluate(Resource resource, String fileName ){
		addLogToBindings(resource)
		evaluate(resource.URL.newReader(), fileName)
	}

	def addLogToBindings(Resource resource){
		if ( !this.getVariable('log') ){
			try {  
				log.debug "getting log name from ${resource.URL.toString()}" 
				String logName = LOG_PREFIX + resource.URL.toString().find(~/(fixtures\/.*).groovy$/){ expression, path	 -> return path }?.replaceAll('/','.')
				this.setVariable( 'log', LogFactory.getLog( logName ) )
				log.debug "Log $logName added to fixture @ $resource"
			} catch (e) {
			   log.error "Unable to create the 'log' property for fixture at $resource"
			}
		}
	}
}