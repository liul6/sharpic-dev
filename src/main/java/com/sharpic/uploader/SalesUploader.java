package com.sharpic.uploader;

import com.sharpic.common.DateUtil;
import com.sharpic.common.SharpICException;
import com.sharpic.common.Util;
import com.sharpic.dao.RecipeDao;
import com.sharpic.dao.SaleDao;
import com.sharpic.domain.AuditMapper;
import com.sharpic.domain.Recipe;
import com.sharpic.domain.Sale;
import com.sharpic.service.IServerCache;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

/**
 * Created by joey on 2016-12-15.
 */

@Service
@ManagedResource
public class SalesUploader {
    private static Log log = LogFactory.getLog(SalesUploader.class.getName());

    private static String[] CIBO_COLUMN_HEADERS = {"Rank", "Num", "Item Name", "Sold", "Sold", "Amount", "Cost",
            "Profit", "Cost %", "Sales"};
    private static String[] VALANTE_COLUMN_HEADERS = {"PLU", "Menu Item", "Gross($) ", "#Voids", "Voids($)",
            "#Refunds", "Refunds($)", "Discounts($)", "Price Levels($)", "Options($)", "Quantity", "Net($)"};

    private enum REPORT_TYPE {
        CIBO_YONGE, VALANTE, UNKNOWN
    }

    ;

    private static final String DELIMITER = ",(?=([^\"]*\"[^\"]*\")*[^\"]*$)";

    @Autowired
    private IServerCache serverCache;

    @Autowired
    private AuditMapper auditMapper;

    @Autowired
    private RecipeDao recipeDao;

    @Autowired
    private SaleDao saleDao;

    @ManagedOperation
    public List<Sale> uploadSales(String clientName, String auditDateStr, InputStream inputStream) throws SharpICException {
        BufferedReader br = null;

        try {
            br = new BufferedReader(new InputStreamReader(inputStream));

            String line = br.readLine();
            line = line.replaceAll("\t", ",");
            REPORT_TYPE reportType = getReportType(line);
            if (reportType == REPORT_TYPE.UNKNOWN) {
                throw new SharpICException("The file has incorrect report type, cannot be parsed!");
            }
            int lineno = 2;

            int auditId = auditMapper.getAuditId(clientName, DateUtil.fromString(auditDateStr, "yyyy-MM-dd"));
            List<Sale> sales = new ArrayList<Sale>();
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("\t", ",");
                String tokens[] = line.split(DELIMITER);

                if (tokens.length <= 0 || tokens[0] == null || tokens[0].trim().length() <= 0)
                    continue;

                Sale sale = extractSale(lineno, auditId, clientName, tokens, reportType);
                if (sale != null) {
                    sale.setAuditId(auditId);
                    sales.add(sale);
                }

                lineno++;
            }

            return saleDao.uploadSales(clientName, auditId, sales);
        } catch (Exception e) {
            throw new SharpICException("The selected file has incorrect content, cannot be parsed!", e);
        }
    }

    private Sale extractSale(int lineNo, int auditId, String clientName, String[] tokens, REPORT_TYPE reportType) throws SharpICException {
        if (tokens == null || reportType == null)
            return null;

        if (reportType == REPORT_TYPE.CIBO_YONGE) {
            if (tokens.length < 7)
                throw new SharpICException("Cannot parse the file at line no# = " + lineNo);
            else {
                Vector row = new Vector();
                row.add(tokens[2]);
                int amount = Integer.valueOf(tokens[3]);
                double gross = Double.valueOf(tokens[5].replace(",", "").replace("\"", ""));
                double price = 0.0D;

                row.add(amount);
                if (Math.abs(amount) == 0)
                    return null;
                else
                    price = Util.round(gross / amount, 2);

                return createSale(lineNo, auditId, clientName, tokens[2], amount, price);
            }
        } else if (reportType == REPORT_TYPE.VALANTE) {
            if (tokens.length <= 11)
                throw new SharpICException("Cannot parse the file at line no# = " + lineNo);
            else if (!"Grand Total".equalsIgnoreCase(tokens[0])) {
                Vector row = new Vector();
                row.add(tokens[1]);
                int amount = Double.valueOf(tokens[10]).intValue();
                double gross = Double.valueOf(tokens[2].replace(",", "").replace("\"", ""));
                double price = 0.0D;

                row.add(amount);
                if (Math.abs(amount) == 0)
                    return null;
                else
                    price = Util.round(gross / amount, 2);

                return createSale(lineNo, auditId, clientName, tokens[1], amount, price);
            }
        }
        return null;
    }

    private static REPORT_TYPE getReportType(String header) {
        if (header == null || header.length() <= 0)
            return REPORT_TYPE.UNKNOWN;

        String columns[] = header.split(DELIMITER);

        if (columns == null || columns.length <= 0)
            return REPORT_TYPE.UNKNOWN;

        boolean match = true;
        if (columns.length == CIBO_COLUMN_HEADERS.length) {
            for (int i = 0; i < columns.length; i++) {
                if (!columns[i].equalsIgnoreCase(CIBO_COLUMN_HEADERS[i])) {
                    match = false;
                }
            }
            if (match)
                return REPORT_TYPE.CIBO_YONGE;
        }

        match = true;
        if (columns.length == VALANTE_COLUMN_HEADERS.length) {
            for (int i = 0; i < columns.length; i++) {
                if (!columns[i].equalsIgnoreCase(VALANTE_COLUMN_HEADERS[i])) {
                    match = false;
                }
            }
            if (match)
                return REPORT_TYPE.VALANTE;
        }

        return REPORT_TYPE.UNKNOWN;
    }

    private Sale createSale(int lineNo, int auditId, String clientName, String recipeName, int amount, double price) throws SharpICException {
        if (!Util.isValidName(recipeName)) {
            throw new SharpICException("The recipe name at line no#= " + lineNo + " is invlalid!");
        }

        Recipe recipe = serverCache.findRecipeByName(clientName, recipeName);
        if (recipe == null) {
            recipe = recipeDao.createRecipe(clientName, recipeName);
        }

        Sale sale = new Sale();
        sale.setRecipeId(recipe.getId());
        sale.setRecipe(recipe);
        sale.setAmount(amount);
        sale.setPrice(price);

        return sale;
    }
}