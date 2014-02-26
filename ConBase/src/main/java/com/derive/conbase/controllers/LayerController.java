package com.derive.conbase.controllers;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.derive.conbase.model.ConbaseOutput;
import com.derive.conbase.model.JqGridData;
import com.derive.conbase.model.Layer;
import com.derive.conbase.model.LayerAttributeConfig;
import com.derive.conbase.model.LayerChartEntry;
import com.derive.conbase.service.LayerService;

@Controller
public class LayerController {

	@Autowired
	LayerService layerService;

	@Transactional
	@RequestMapping(value = "/getLayers", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getLayers(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Layer> layers = layerService.getActiveLayers();
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(layers);
		return output;
	}

	@Transactional
	@RequestMapping(value = "/getItems", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getItems(@RequestParam Short type, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<Layer> layers = layerService.getActiveItems(type);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(layers);
		return output;
	}

	@Transactional
	@RequestMapping(value = "/getLayersByPartialName", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getLayersByPartialName(@RequestParam String name,
			Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		List<Layer> layers = layerService.getLayersByPartialName(name);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(layers);
		return output;
	}

	@Transactional
	@RequestMapping(value = "/getLayerChartEntriesByConfigIdByFromByTo", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getLayerChartEntriesById(
			@RequestParam Long layerAttributeConfigId,
			@RequestParam Integer from, @RequestParam Integer to, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		List<LayerChartEntry> entries = layerService
				.getLayerChartEntriesByConfigIdByFromByTo(
						layerAttributeConfigId, from, to);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(entries);
		return output;
	}

	@Transactional
	@RequestMapping(value = "/getLayerById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getLayerById(@RequestParam Long layerId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Layer layer = layerService.getLayersWithAttributesByLayerId(layerId);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(layer);
		return output;
	}

	@Transactional
	@RequestMapping(value = "/getItemById", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput getItemById(@RequestParam Long itemId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Layer layer = layerService.getLayersWithAttributesByLayerId(itemId);
		ConbaseOutput output = new ConbaseOutput();
		output.setSuccess(true);
		output.setOutput(layer);
		return output;
	}

	@Transactional
	@RequestMapping(value = "/addItem", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput addItem(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Layer layer = mapper.readValue(requestString, Layer.class);
		layer.setActive(true);
		ConbaseOutput output = new ConbaseOutput();
		Map<Boolean, List<String>> validateMap = layer.validate();
		if (validateMap.get(true) != null) {
			Layer existingLayer = layerService.getActiveLayerByName(layer
					.getName());
			if (existingLayer == null) {
				layerService.addLayer(layer);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays
						.asList(new String[] { "Item of same name is already existing!" }));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}

		return output;
	}

	@Transactional
	@RequestMapping(value = "/updateItem", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput updateItem(@RequestBody String requestString, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		Layer layer = mapper.readValue(requestString, Layer.class);
		ConbaseOutput output = new ConbaseOutput();
		Map<Boolean, List<String>> validateMap = layer.validate();
		if (validateMap.get(true) != null) {
			Layer existingLayer = layerService.getActiveLayerByName(layer
					.getName());
			if (existingLayer == null
					|| existingLayer.getId().equals(layer.getId())) {
				layerService.updateLayer(layer);
				output.setSuccess(true);
			} else {
				output.setSuccess(false);
				output.setMessages(Arrays
						.asList(new String[] { "Layer of same name is already existing!" }));
			}
		} else {
			output.setSuccess(false);
			output.setMessages(validateMap.get(false));
		}
		return output;
	}

	@Transactional
	@RequestMapping(value = "/deactivateLayer", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput deactivateLayer(@RequestParam Long layerId, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		if (layerId == null) {
			output.setSuccess(false);
			output.setMessages(Arrays
					.asList(new String[] { "No layer supplied!" }));
		} else {
			layerService.deactivate(layerId);
			output.setSuccess(true);
		}

		return output;

	}

	@Transactional
	@RequestMapping(value = "/getJqItems", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	JqGridData<Layer> getJqItems(@RequestParam Short type, Model model,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		JqGridData<Layer> gridData = layerService.getItemsByPageByType(0, 0,
				type);
		return gridData;
	}

	@Transactional
	@RequestMapping(value = "/getJqLayers", method = RequestMethod.GET, produces = "application/json")
	public @ResponseBody
	JqGridData<Layer> getJqLayers(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		JqGridData<Layer> gridData = layerService.getLayersByPage(0, 0);
		return gridData;
	}

	@RequestMapping(value = "/deleteLayerChart", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput deleteLayerChart(@RequestParam Long layerAttributeConfigId, Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		layerService.deleteLayerChart(layerAttributeConfigId);
		output.setSuccess(true);
		return output;
	}
	
	@RequestMapping(value = "/addLayerChart", method = RequestMethod.POST, produces = "application/json")
	public @ResponseBody
	ConbaseOutput addLayerChart(Model model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		ConbaseOutput output = new ConbaseOutput();
		LayerAttributeConfig layerAttributeConfig = null;
		FileItem fileItem = null;
		// Check that we have a file upload request
		boolean isMultipart = ServletFileUpload.isMultipartContent(request);
		if (isMultipart) {
			// Create a factory for disk-based file items
			DiskFileItemFactory factory = new DiskFileItemFactory();
			factory.setRepository(new File(""));

			// Create a new file upload handler
			ServletFileUpload upload = new ServletFileUpload(factory);

			// Parse the request
			List<FileItem> items = upload.parseRequest(request);
			// Process the uploaded items
			Iterator<FileItem> iter = items.iterator();
			while (iter.hasNext()) {
				FileItem item = iter.next();
				if (item.isFormField()
						&& "requestString".equals(item.getFieldName())) {
					String requestString = item.getString();
					ObjectMapper mapper = new ObjectMapper();
					mapper.configure(
							DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES,
							false);
					layerAttributeConfig = mapper.readValue(requestString,
							LayerAttributeConfig.class);
					layerService
							.cleanAndOrderLayerAttributeConfig(layerAttributeConfig);
					if (!layerService
							.validateLayerAttributeConfig(layerAttributeConfig)) {
						output.setSuccess(false);
						output.setMessages(Arrays
								.asList(new String[] { "All mandatory layer attributes not provided!" }));
						return output;
					}

				}
				if (!item.isFormField()) {
					fileItem = item;
				}
			}
		}

		if (layerAttributeConfig != null && fileItem != null) {
			LayerAttributeConfig lac = layerService
					.getLayerAttributeConfigByValues(layerAttributeConfig);
			if (lac == null) {
				lac = layerService
						.createLayerAttributeConfigByValues(layerAttributeConfig);
			} else {
				// lac =
				// layerService.createLayerAttributeConfigByValues(layerAttributeConfig);
			}
			processUploadedFile(lac, fileItem);
			output.setSuccess(true);
		} else {
			output.setSuccess(false);
			output.setMessages(Arrays.asList(new String[] { "No layer chart file found!" }));
		}
		return output;
	}

	private void processUploadedFile(LayerAttributeConfig layerAttributeConfig,
			FileItem item) throws IOException {
		layerService.deleteLayerChart(layerAttributeConfig.getId());
		InputStream uploadedStream = item.getInputStream();
		// Get the workbook instance for XLS file
		HSSFWorkbook workbook = new HSSFWorkbook(uploadedStream);
		// Get first sheet from the workbook
		HSSFSheet sheet = workbook.getSheetAt(0);

		// Get iterator to all the rows in current sheet
		Iterator<Row> rowIterator = sheet.iterator();
		int rowNumber = 0;
		List<LayerChartEntry> entries = new ArrayList<LayerChartEntry>();
		while (rowIterator.hasNext()) {
			++rowNumber;
			Row row = rowIterator.next();
			// For each row, iterate through each columns
			if (rowNumber == 1) {
				continue;
			}
			if (rowNumber >= 10001) {
				break;
			}
			Iterator<Cell> cellIterator = row.cellIterator();
			int cellNumber = 0;
			LayerChartEntry entry = new LayerChartEntry();
			while (cellIterator.hasNext()) {
				++cellNumber;
				Cell cell = cellIterator.next();
				Object valueObject = null;
				switch (cell.getCellType()) {
				case Cell.CELL_TYPE_BOOLEAN:
					valueObject = cell.getBooleanCellValue();
					break;
				case Cell.CELL_TYPE_NUMERIC:
					valueObject = (int) cell.getNumericCellValue();
					break;
				case Cell.CELL_TYPE_STRING:
					valueObject = cell.getStringCellValue();
					break;
				}
				if (cellNumber == 1) {
					entry.setFrom(Integer.valueOf(valueObject.toString()));
				} else if (cellNumber == 2) {
					entry.setTo(Integer.valueOf(valueObject.toString()));
				} else if (cellNumber == 3) {
					entry.setLevel(Integer.valueOf(valueObject.toString()));
				} else {
					continue;
				}
			}
			entries.add(entry);
			if (entries.size() > 100) {
				layerService.saveLayerChart(layerAttributeConfig.getId(), entries);
				entries.clear();
			}
		}
		if (entries.size() > 0) {
			layerService.saveLayerChart(layerAttributeConfig.getId(), entries);
		}
	}
}
