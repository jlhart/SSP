<%--

    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:

    http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.

--%>

		<title>Resources - Search for Resources</title> 
		
		<link rel="stylesheet" href="/styles/lib/jquery.mobile-1.0.1.min.css" />
		<link rel="stylesheet" href="/styles/lib/apprise.min.css" />
		<link rel="stylesheet" href="/styles/stylesheet.min.css" />
		<!--[if IE 7]>
			<link rel="stylesheet" href="/styles/win-ie7.min.css">
		<![endif]-->
		<!--[if IE 8]>
			<link rel="stylesheet" href="/styles/win-ie8.min.css">
		<![endif]-->
		
		<link media="only screen and (min-device-width: 768px) and (max-device-width: 1024px)" rel="stylesheet" href="/styles/ipad.min.css" />
		
		<script type="text/javascript" src="/scripts/lib/json2.js"></script>
		<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.6.4/jquery.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/jquery.mobile-1.0rc1.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/jquery.tmpl.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/jquery.tmpl.loadTemplates.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/jquery.simplemodal.1.4.2.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/jquery.parameter.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/underscore-min.js"></script>
		<script type="text/javascript" src="/scripts/lib/knockout-1.2.1.js"></script>
		<script type="text/javascript" src="/scripts/lib/ko.jqm.bindings.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/namespace.min.js"></script>
		<script type="text/javascript" src="/scripts/lib/apprise-1.5.min.js"></script>
		<script type="text/javascript" src="/scripts/MyGPS-2.1.0.min.js"></script>
		
		<!-- Search Page -->
		
		<div id="search-page" class="page" data-role="page">
			
			<script type="text/javascript" src="scripts/search.js"></script>
			
			<div class="header">
				<div class="banner" data-bind="template: { name: 'bannerTemplate' }"></div>
				<div data-role="header" data-theme="b">
					<a href="<portlet:renderURL></portlet:renderURL>" data-icon="home" data-rel="back">Home</a>
					<h1>&nbsp;</h1>
					<a href="login.jsp" rel="external" class="ui-btn-right" data-bind="visible: !viewModel.authenticated()" data-role="button" data-icon="custom">Login</a>
					<p class="person-name" data-bind="text: authenticatedPersonName"></p>
					<a href="../j_spring_security_logout" rel="external" class="ui-btn-right" data-bind="visible: viewModel.authenticated()" data-role="button" data-icon="custom">Logout</a>
				</div>
			</div>
		
			<div class="content" data-role="content">
			
				<div class="referral-search">
					<h2>Search for Resources</h2>
					<p>Use the keyword search and then click 'Go' to find resources and referrals to help you succeed!</p>
					<form id="searchForm" class="search-form" data-ajax="false" data-bind="submit: function () { viewModel.search( viewModel.query() ); }">
						<div data-role="fieldcontain">
						    <label for="query" style="margin-top: 7px;">Assist me with:</label>
						    <input type="search" name="query" data-bind="value: query, valueUpdate: 'afterkeydown'" />
						    <button data-bind="click: function () { $('#searchForm').trigger('submit'); }" data-inline="true">Go</button>
						</div>
					</form>
					<div class="drill-down-container" data-bind="css: { drilled: selectedChallenge }">
						<div class="drill-down-master-view">
							<div class="ui-bar-b ui-header" data-theme="b">
								<h1 class="ui-title">Challenges</h1>
								<span class="header-count ui-btn-up-b ui-btn-right ui-btn-corner-all" data-bind="text: viewModel.challenges().length"></span>
							</div>
							<ul data-role="listview" data-inset="true" data-bind="template: { name: 'challengeTemplate', foreach: challenges }">
							</ul>
						</div>
						<div class="drill-down-detail-view">
							<div class="ui-bar-b ui-header" data-theme="b">
								<a href="#" rel="external" class="ui-btn-left" data-bind="click: function () { viewModel.selectChallenge( null ); }" data-icon="arrow-l">Challenges</a>
								<h1 class="ui-title" data-bind="text: selectedChallengeName"></h1>
								<span class="header-count ui-btn-up-b ui-btn-right ui-btn-corner-all" data-bind="text: viewModel.filteredReferrals().length"></span>
							</div>
							<ul data-role="listview" data-inset="true" data-bind="template: { name: 'referralTemplate', foreach: filteredReferrals }">
							</ul>
						</div>
					</div>
					<span style="display: none" data-bind="visible: allowCustomTaskCreation">					
						<button data-bind="click: function () { var container = $('<div></div>'); ko.renderTemplate( 'customTaskTemplate', viewModel, { templateOptions: { modal: container.modal() } }, container.get(0) ); container.trigger('create'); }" data-icon="plus" data-inline="true">Create Custom Task...</button>
					</span>
				</div>
				
				<div class="task-list" data-bind="template: { name: 'tasksTemplate' }">
				</div>
				
			</div>

			<div class="footer" data-bind="template: { name: 'footerTemplate' }"></div>
			
		</div>
		