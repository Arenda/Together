(function(window, undefined) {
var Together = window.Together = function() {
	var args = Array.prototype.slice.call(arguments),
		callback = args.pop(),
		modules = (args[0] && typeof args[0] === 'string') ? args : args[0],
		i;
	if(!(this instanceof Together)) {
		return new Together(modules, callback);
	}
	
	if(!modules || modules === '*') {
		modules = [];
		for(i in Together.android) {
			if(Together.android.hasOwnProperty(i)) {
				modules.push(i);
			}
		}
	}
	
	for(i = 0; i < modules.length; ++i) {
		Together.android[modules[i]](this);
	}
	
	callback(this);
}

Together.HOST = 'http://220.133.188.197:81/Together/';

Together.prototype = (function() {
	return {
	    name: "Let's Together!",
	    version: "1.0"
	}
})();

Together.android = {};
})(window);