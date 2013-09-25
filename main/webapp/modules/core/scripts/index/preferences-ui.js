Refine.SetLanguageUI = function(elmt) {
	elmt.html(DOM.loadHTML("core", "scripts/index/preferences-ui.html"));

	this._elmt = elmt;
	this._elmts = DOM.bind(elmt);

	this._elmts.or_lang_label.text($.i18n._('core-index-pref')["lang-label"]
			+ ":");
	this._elmts.set_lan_btn.attr("value",
			$.i18n._('core-index-pref')["lang-change"]);
	this._elmts.or_comp_label.text($.i18n._('core-index-pref')["comp-label"]);
	this._elmts.set_comp_btn.attr("value",
			$.i18n._('core-index-pref')["comp-change"]);

	$.ajax({
		url : "/command/core/get-languages?",
		type : "GET",
		async : false,
		data : {
			name : "module",
			value : "core"
		},
		success : function(data) {
			for ( var i = 0; i < data.languages.length; i++) {
				var l = data.languages[i];
				$('<option>').val(l.code).text(l.label).appendTo('#langDD');
			}
		}

	});
	
	// Adding compression levels
	var levels = [ "No", "Min", "Average", "Max" ];
	for ( var i = 0; i < 4; i++) {
		$('<option>').val(i).text(levels[i]).appendTo('#compDD');
	}

	this._elmts.set_lan_btn.bind('click', function(e) {
		$.ajax({
			url : "/command/core/set-preference?",
			type : "POST",
			async : false,
			data : {
				name : "userLang",
				value : $("#langDD option:selected").val()
			},
			success : function(data) {
				alert($.i18n._('core-index-pref')["page-reload"]);
				location.reload(true);
			}
		});
	});
	
	this._elmts.set_comp_btn.bind('click', function(e) {
		$.ajax({
			url : "/command/core/set-preference?",
			type : "POST",
			async : false,
			data : {
				name : "compression",
				value : $("#compDD option:selected").val()
			},
			success : function(data) {
				alert($.i18n._('core-index-pref')["comp-set"]);
			}
		});
	});
};

Refine.SetLanguageUI.prototype.resize = function() {
};

Refine.actionAreas.push({
	id : "lang-settings",
	label : $.i18n._('core-index-pref')["label"],
	uiClass : Refine.SetLanguageUI
});
