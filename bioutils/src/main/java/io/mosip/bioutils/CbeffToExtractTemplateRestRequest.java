package io.mosip.bioutils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mosip.bioutils.dto.ExtractTemplateRequestDto;
import io.mosip.bioutils.dto.RequestDto;
import io.mosip.bioutils.utils.Util;
import io.mosip.kernel.biometrics.constant.BiometricType;
import io.mosip.kernel.biometrics.constant.ProcessedLevelType;
import io.mosip.kernel.biometrics.entities.BDBInfo;
import io.mosip.kernel.biometrics.entities.BIR;
import io.mosip.kernel.biometrics.entities.BiometricRecord;
import io.mosip.kernel.biometrics.entities.VersionType;
import org.json.JSONObject;
import org.json.XML;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static io.mosip.bioutils.utils.Util.xmlToJson;
import static java.lang.Integer.parseInt;

public class CbeffToExtractTemplateRestRequest {

    private static final String VERSION = "1.0";
    private static Logger LOGGER = LoggerFactory.getLogger(CbeffToExtractTemplateRestRequest.class);
    private static int PRETTY_PRINT_INDENT_FACTOR = 4;
    private static final String dir = System.getProperty("user.dir");

    public static void main(String [] args) throws IOException, ParserConfigurationException, SAXException {
        String samplePath = CbeffToExtractTemplateRestRequest.class.getResource("/sample.cbeff.xml").getPath();
        BiometricRecord sample = Util.xmlFileToBiometricRecord(samplePath);
        Gson gson = new GsonBuilder().serializeNulls().create();
        LOGGER.info(gson.toJson(sample));

        List<BiometricType> modalitiesToExtract = new ArrayList<BiometricType>(){{
//            add(BiometricType.FACE);
//            add(BiometricType.FINGER);
            add(BiometricType.IRIS);
        }};

        ExtractTemplateRequestDto extractTemplateRequestDto = new ExtractTemplateRequestDto();
        extractTemplateRequestDto.setSample(sample);
        extractTemplateRequestDto.setModalitiesToExtract(modalitiesToExtract);
        extractTemplateRequestDto.setFlags(new HashMap<>());

        RequestDto requestDto = new RequestDto();
        requestDto.setVersion(VERSION);
        requestDto.setRequest(Util.base64Encode(gson.toJson(extractTemplateRequestDto)));

        String requstJson = gson.toJson(requestDto);
        LOGGER.info(requstJson);

        Path resultFile = Paths.get(dir, "bioutils", "output", "extractRequest.json");
        LOGGER.info(resultFile.toString());
        Util.textToFile(resultFile.toString(), requstJson);
    }


}
