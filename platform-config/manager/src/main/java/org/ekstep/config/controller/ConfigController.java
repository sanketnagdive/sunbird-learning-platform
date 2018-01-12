package org.ekstep.config.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.ekstep.common.controller.BaseController;
import org.ekstep.common.dto.Response;
import org.ekstep.common.dto.ResponseParams;
import org.ekstep.common.dto.ResponseParams.StatusType;
import org.ekstep.common.exception.ResponseCode;
import org.ekstep.common.util.AWSUploader;
import org.ekstep.common.util.HttpDownloadUtility;
import org.ekstep.telemetry.logger.TelemetryManager;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("v2/config")
public class ConfigController extends BaseController {
	private ObjectMapper mapper = new ObjectMapper();
	public static final String folderName = "resources";
	public static final String baseUrl = "https://" + AWSUploader.getBucketName("config") + ".s3.amazonaws.com/";

	@RequestMapping(value = "/resourcebundles", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getResourceBundles() {
		String apiId = "ekstep.config.resourebundles.list";
		try {
			Response response = new Response();
			Map<String, Object> resourcebundles = new HashMap<String, Object>();
			Map<String, String> urlMap = getUrlFromS3();
			TelemetryManager.log("urls of resourcebundle files from s3: "+ urlMap);
			for (Entry<String, String> entry : urlMap.entrySet()) {
				String langMap = HttpDownloadUtility.readFromUrl(entry.getValue());
				String langId = entry.getKey();
				try {
					if (StringUtils.isBlank(langMap))
						continue;
					Map<String, Object> map = mapper.readValue(langMap, new TypeReference<Map<String, Object>>() {
					});
					TelemetryManager.log("Resourcebundles fetched: "+ map.keySet());
					resourcebundles.put(langId, map);
				} catch (Exception e) {
					TelemetryManager.error("Error in fetching all ResourceBundles from s3: "+ e.getMessage(), e);
				}
			}
			response.put("resourcebundles", resourcebundles);
			ResponseParams params = new ResponseParams();
			params.setErr("0");
			params.setStatus(StatusType.successful.name());
			params.setErrmsg("Operation successful");
			response.setParams(params);
			response.put("ttl", 24.0);
			TelemetryManager.log("get All ResourceBundles | Response: " + response + " Id" + apiId);
			return getResponseEntity(response, apiId, null);
		} catch (Exception e) {
			TelemetryManager.error("getAllResources | Exception" + e.getMessage(), e);
			return getExceptionResponseEntity(e, apiId, null);
		}
	}

	@RequestMapping(value = "/resourcebundles/{languageId}", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getResourceBundle(@PathVariable(value = "languageId") String languageId) {
		String apiId = "ekstep.config.resourebundles.info";

		try {
			TelemetryManager.log("ResourceBundle | GET | languageId" + languageId);
			Response response = new Response();
			String data = HttpDownloadUtility.readFromUrl(baseUrl + folderName + "/" + languageId + ".json");
			TelemetryManager.log("Resource bundle file read from url:" + StringUtils.isNotBlank(data));
			if (StringUtils.isNotBlank(data)) {
				ResponseParams params = new ResponseParams();
				params.setErr("0");
				params.setStatus(StatusType.successful.name());
				params.setErrmsg("Operation successful");
				response.setParams(params);
				response.put("ttl", 24.0);
				try {
					Map<String, Object> map = mapper.readValue(data, new TypeReference<Map<String, Object>>() {
					});
					response.put(languageId, map);
					TelemetryManager.log("getResourceBundle | successResponse: " , response.getResult());
				} catch (Exception e) {
					TelemetryManager.error("getResourceBundle | Exception" + e.getMessage(), e);
				}
				return getResponseEntity(response, apiId, null);
			} else {
				ResponseParams params = new ResponseParams();
				params.setErr("1");
				params.setStatus(StatusType.failed.name());
				params.setErrmsg("Operation failed");
				response.setParams(params);
				response.getResponseCode();
				response.setResponseCode(ResponseCode.RESOURCE_NOT_FOUND);
				TelemetryManager.warn("getResourceBundle | FailureResponse" , response.getResult());
				return getResponseEntity(response, apiId, null);
			}
		} catch (Exception e){
			TelemetryManager.error("getResourceBundle | Exception: " + e.getMessage(), e);
			return getExceptionResponseEntity(e, apiId, null);
		}
	}

	@RequestMapping(value = "/ordinals", method = RequestMethod.GET)
	@ResponseBody
	public ResponseEntity<Response> getOrdinals() {
		String apiId = "ekstep.config.ordinals.list";
		String ordinals = "";
		Response response = new Response();
		try {
			ordinals = HttpDownloadUtility.readFromUrl(baseUrl + "ordinals.json");
			TelemetryManager.log("Ordinals data read from s3 url" + StringUtils.isNotBlank(ordinals));
			ResponseParams params = new ResponseParams();
			params.setErr("0");
			params.setStatus(StatusType.successful.name());
			params.setErrmsg("Operation successful");
			response.setParams(params);
			response.put("ttl", 24.0);
			try {
				Map<String, Object> map = mapper.readValue(ordinals, new TypeReference<Map<String, Object>>() {
				});
				response.put("ordinals", map);
			} catch (Exception e) {
				TelemetryManager.error("Get Ordinals | Exception: " + e.getMessage(), e);
			}
			TelemetryManager.log("Get Ordinals | Response" + response.getResponseCode());
			return getResponseEntity(response, apiId, null);
		} catch (Exception e) {
				TelemetryManager.error("getOrdinalsException" + e.getMessage(), e);
				return getExceptionResponseEntity(e, apiId, null);
		}
	}

	private Map<String, String> getUrlFromS3() throws JsonProcessingException {
		Map<String, String> urlList = new HashMap<String, String>();
		String apiUrl = "";
		List<String> res = AWSUploader.getObjectList(folderName, "config");
		TelemetryManager.log("ResourceBundle Urls fetched from s3" + res.size());
		for (String data : res) {
			if (StringUtils.isNotBlank(FilenameUtils.getExtension(data))) {
				apiUrl = baseUrl + data;
				urlList.put(FilenameUtils.getBaseName(data), apiUrl);
			}
		}
		return urlList;
	}
}