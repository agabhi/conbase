/**
 * Created with JetBrains WebStorm.
 * User: Shree
 * Date: 11/30/13
 * Time: 4:13 PM
 * To change this template use File | Settings | File Templates.
 */



(function ( $ ) {
 
    $.fn.layerchart = function( options ) {
 
        // This is the easiest way to have default options.
        var settings = $.extend({
            // These are the defaults.
            from : 0,
            noOfBlocks : 100,
            interval : 10
        }, options );
        
        var rows = [];
        var maxRowNumber = 0;
        
        function getTopHeader() {
        	var header ="<thead><tr><th></th>";
        	for(var i = 0; i < settings.noOfBlocks; i = i + 1) {
        	    var section = settings.from + (i*settings.interval);
        		header += "<th><div class='rotate'>"+section+"</div></th>";
        	}
        	header += "</tr></thead>";
        	return header;
        }
        
        function getBody() {
        	var body = "<tbody>";
        	for (var l =maxRowNumber; l > 0; --l) {
        		body += "<tr>";
        		body +='<td style="width:200px;min-width:200px;max-width:400px;">'+settings.data.layerAttributeConfig.layer.name+" - "+l+"</td>"
        		for(var i = 0; i < settings.noOfBlocks; i = i + 1) {
            	    var found = false;
        			for (var k = l; k <= maxRowNumber; ++k) {
	        			if (rows[k] && rows[k][i]) {
	            	    	found = true;
	            	    	break;
	            	    }
            	    }
        			if (found) {
            	    	body += '<td style="background:rgb(154, 187, 201);"></td>';
            	    } else {
            	    	body += '<td class="no-border"></td>';
            	    }
            	}
        		body += "</tr>";
        	}
        	body += "</tbody>";
        	return body;
        }
        
        function processData() {
        	if (settings.data && settings.data.entries) {
        		$.each(settings.data.entries, function( index, entry ) {
        			if (entry.level > 0 && entry.to > (entry.from + settings.interval)) {
        				var normalizedFrom = settings.from + Math.ceil((entry.from - settings.from)/settings.interval)*settings.interval;
        				for (var i = normalizedFrom; i < entry.to; i = i +settings.interval) {
        					var index = (i- settings.from)/settings.interval;
        					initializeRow(entry.level);
        					if (!rows[entry.level]) {
        						rows[entry.level] = {};
        					}
        					rows[entry.level][index] = entry; 
        				}
        			}
        		});
        	}
        }
        function initializeRow(rowNumber) {
        	if (rowNumber > maxRowNumber) {
        		maxRowNumber = rowNumber;
        	}
        	/*
        	if(rowNumber > maxRowNumber) {
        		for (i = maxRowNumber+1; i <= rowNumber; ++i) {
        			for(var j = settings.from; j < settings.to; j = j + settings.interval) {
                	    rows[i].j = {};
                	}
        		}
        	}
        	*/
        }
        
        processData();
        var html = '<table class="layer-chart">'+getTopHeader()+getBody()+'</table>';
        this.html(html);
        
 
    };
 
}( jQuery ));






(function ( $ ) {
	 
    $.fn.barchart = function( options ) {
 
        // This is the easiest way to have default options.
        var settings = $.extend({
            // These are the defaults.
            from : 0,
            noOfBlocks : 100,
            interval : 10
        }, options );
        
        var rows = {};
        var maxRowNumber = {};
        var colorMap = {};
        var legendsHtml = '';
            
        function createColorLegends() {
        	var seriesColors = ["#4bb2c5", "#EAA228", "#c5b47f", "#579575", "#839557", "#958c12", "#953579", "#4b5de4", "#d8b83f", "#ff5800", "#0085cc", "#c747a3", "#cddf54", "#FBD178", "#26B4E3", "#bd70c7"];
        	var seriesColorsLength = seriesColors.length;
        	var options = settings.barchart.recordTypeAttribute.options;
        	legendsHtml += '<div class="clearfix" style="margin-bottom:10px;">';
        	$.each(options, function( index, option) {
        		colorMap[option] = seriesColors[index%seriesColorsLength];
        		legendsHtml += '<div style="display:inline-block;width:12px;height:12px;margin-right:5px;background:'+ colorMap[option] + '">&nbsp;</div><div style="display:inline-block;margin-right:20px;">' + option+"</div>";
        	});
        	legendsHtml += '</div>';
        	settings.barchart.recordTypeAttribute.colorMap = colorMap;
        }
        
        function getTopHeader() {
        	var header ="<thead><tr><th></th>";
        	for(var i = 0; i < settings.noOfBlocks; i = i + 1) {
        	    var section = settings.from + (i*settings.interval);
        		header += "<th><div class='rotate'>"+section+"</div></th>";
        	}
        	header += "</tr></thead>";
        	return header;
        }
        
        function getBody() {
        	var layerAttributeConfigs = settings.barchart.layerAttributeConfigs;
        	var reverseLayerAttributeConfigs = layerAttributeConfigs.slice(0);
        	reverseLayerAttributeConfigs.reverse();
        	
        	var body = "<tbody>";
        	$.each(reverseLayerAttributeConfigs, function( index, layerAttributeConfig) {
	        	var maxRow = maxRowNumber[layerAttributeConfig.id];
        		if (maxRow) {
		        	for (var l =maxRow; l > 0; --l) {
		        		body += "<tr>";
		        		body +='<td style="width:200px;min-width:200px;max-width:400px;">'+settings.layersMap[layerAttributeConfig.layer.id].name+" - "+l+"</td>"
		        		for(var i = 0; i < settings.noOfBlocks; i = i + 1) {
		            	    var found = false;
		        			for (var k = l; k <= maxRow; ++k) {
			        			if (rows[layerAttributeConfig.id] && rows[layerAttributeConfig.id][k] && rows[layerAttributeConfig.id][k][i]) {
			            	    	found = true;
			            	    	break;
			            	    }
		            	    }
		        			if (found) {
		            	    	if (rows[layerAttributeConfig.id][k][i].attributeValue) {
		            	    		var color = colorMap[rows[layerAttributeConfig.id][k][i].attributeValue];
		            	    		if (color) {
		            	    			body += '<td style="background:'+color+'"></td>';
		            	    		} else {
		            	    			body += '<td style="background:rgb(154, 187, 201);"></td>';
		            	    		}
		            	    	}
		            	    } else {
		            	    	body += '<td class="no-border"></td>';
		            	    }
		            	}
		        		body += "</tr>";
		        	}
        		}
        	});
        	body += "</tbody>";
        	return body;
        }
        
        function processData() {
        	if (settings.data) {
        		var recordAttributeMap = {};
        		if (settings.data.recordAttributes) {
	        		$.each(settings.data.recordAttributes, function( index, recordAttribute ) {
	        			recordAttributeMap[recordAttribute.recordId] = recordAttribute;
	        		});
        		}
        		if (settings.data.layerChartEntries) {
	        		$.each(settings.data.layerChartEntries, function( index, entry ) {
	        			if (entry.level > 0 && entry.to > (entry.from + settings.interval)) {
	        				var normalizedFrom = settings.from + Math.ceil((entry.from - settings.from)/settings.interval)*settings.interval;
	        				for (var i = normalizedFrom; i < entry.to; i = i +settings.interval) {
	        					var index = (i- settings.from)/settings.interval;
	        					initializeRow(entry.layerAttributeConfigId, entry.level);
	        					if (!rows[entry.layerAttributeConfigId]) {
	        						rows[entry.layerAttributeConfigId] = {};
	        					}
	        					if (!rows[entry.layerAttributeConfigId][entry.level]) {
	        						rows[entry.layerAttributeConfigId][entry.level] = {};
	        					}
	        					rows[entry.layerAttributeConfigId][entry.level][index] = {}; 
	        				}
	        			}
	        		});
        		}
        		if (settings.data.records) {
	        		$.each(settings.data.records, function( index, record ) {
	        			if (record.level > 0 && record.to > (record.from + settings.interval)) {
	        				var normalizedFrom = settings.from + Math.ceil((record.from - settings.from)/settings.interval)*settings.interval;
	        				for (var i = normalizedFrom; i < record.to; i = i +settings.interval) {
	        					var index = (i- settings.from)/settings.interval;
	        					initializeRow(record.layerAttributeConfig.id, record.level);
	        					if (!rows[record.layerAttributeConfig.id]) {
	        						rows[record.layerAttributeConfig.id] = {};
	        					}
	        					if (!rows[record.layerAttributeConfig.id][record.level]) {
	        						rows[record.layerAttributeConfig.id][record.level] = {};
	        					}
	        					rows[record.layerAttributeConfig.id][record.level][index] = {};
	        					rows[record.layerAttributeConfig.id][record.level][index].recordId = record.id;
	        					if (recordAttributeMap[record.id]) {
	        						rows[record.layerAttributeConfig.id][record.level][index].attributeValue = recordAttributeMap[record.id].value;
	        					}
	        				}
	        			}
	        		});
        		}
        	}
        }
        function initializeRow(layerAttributeConfigId, rowNumber) {
        	if (!maxRowNumber[layerAttributeConfigId]) {
        		maxRowNumber[layerAttributeConfigId] = 0;
        	}
        	if (rowNumber > maxRowNumber[layerAttributeConfigId]) {
        		maxRowNumber[layerAttributeConfigId] = rowNumber;
        	}
        	/*
        	if(rowNumber > maxRowNumber) {
        		for (i = maxRowNumber+1; i <= rowNumber; ++i) {
        			for(var j = settings.from; j < settings.to; j = j + settings.interval) {
                	    rows[i].j = {};
                	}
        		}
        	}
        	*/
        }
        
        processData();
        createColorLegends();
        var html = legendsHtml + '<table class="layer-chart">'+getTopHeader()+getBody()+'</table>';
        this.html(html);
        
 
    };
 
}( jQuery ));