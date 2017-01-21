package com.sharpic.controller;

import com.sharpic.common.Util;
import com.sharpic.domain.Sale;
import com.sharpic.service.IServerCache;
import com.sharpic.uploader.SalesUploader;
import com.sharpic.web.SharpICResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by joey on 2016-12-14.
 */

@Controller
public class FileUploadController {
    private static Log log = LogFactory.getLog(FileUploadController.class.getName());

    @Autowired
    private SalesUploader salesUploader;

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private SaleController saleController;

    @RequestMapping(value = "/upload", method = RequestMethod.GET)
    @ResponseBody
    public String provideUploadInfo() {
        return "You can upload a file by posting to this same URL.";
    }

    @RequestMapping(value = ("/upload"), headers = ("content-type=multipart/*"), method = RequestMethod.POST)
    @ResponseBody
    public SharpICResponse handleFileUpload(@RequestParam("name") String category,
                                            @RequestParam("clientName") String clientName,
                                            @RequestParam("auditDateStr") String auditDateStr,
                                            @RequestParam("file") MultipartFile file) {
        SharpICResponse sharpICResponse = new SharpICResponse();

        try {
            if (category != null && !file.isEmpty()) {
                if ("SALE".equalsIgnoreCase(category)) {
                    uploadSale(clientName, auditDateStr, file);

                    return saleController.getSales(clientName, auditDateStr);
                }
            }
        } catch (Exception e) {
            sharpICResponse.setErrorText(e.getMessage());
        }

        return sharpICResponse;
    }

    private List<Sale> uploadSale(String clientName, String auditDateStr, MultipartFile file) {
        if (Util.isValidName(clientName) && Util.isValidName(auditDateStr))
            try {
                List<Sale> sales = salesUploader.uploadSales(clientName, auditDateStr, file.getInputStream());
                if (sales == null || sales.size() <= 0)
                    serverCache.fillRecipeCache();
                return sales;
            } catch (Exception e) {
                log.error(e);
            }
        return new ArrayList<Sale>();
    }
}