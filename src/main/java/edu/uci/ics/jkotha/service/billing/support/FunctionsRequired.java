package edu.uci.ics.jkotha.service.billing.support;

import edu.uci.ics.jkotha.service.billing.logger.ServiceLogger;
import edu.uci.ics.jkotha.service.billing.models.BasicResponseModel;
import edu.uci.ics.jkotha.service.billing.models.CartInsertUpdateReqModel;
import edu.uci.ics.jkotha.service.billing.models.ItemModel;
import org.glassfish.jersey.internal.util.ExceptionUtils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class FunctionsRequired {

    public static String getMessage(int resultCode) {
        switch (resultCode) {
            case -11:
                return "Email address has invalid format.";
            case -10:
                return "Email address has invalid length.";
            case -3:
                return "JSON Parse Exception.";
            case -2:
                return "JSON Mapping Exception.";
            case 33:
                return "Quantity has invalid value.";
            case 311:
                return " Duplicate insertion.";
            case 3100:
                return "Shopping cart item inserted successfully.";
            case 312:
                return "Shopping item does not exist.";
            case 3110:
                return "Shopping cart item updated successfully.";
            case 3120:
                return "Shopping cart item deleted successfully.";
            case 3130:
                return "Shopping cart retrieved successfully.";
            case 3140:
                return "Shopping cart cleared successfully.";
            case 321:
                return "Credit card ID has invalid length.";
            case 322:
                return "Credit card ID has invalid value.";
            case 323:
                return "expiration has invalid value.";
            case 3200:
                return "Credit card inserted successfully.";
            case 324:
                return "Credit card does not exist.";
            case 325:
                return "Duplicate insertion.";
            case 3210:
                return "Credit card updated successfully.";
            case 3220:
                return "Credit card deleted successfully.";
            case 3230:
                return "Credit card retrieved successfully.";
            case 331:
                return "Credit card ID not found.";
            case 3300:
                return "Customer inserted successfully.";
            case 332:
                return "Customer does not exist.";
            case 333:
                return "Duplicate insertion.";
            case 3310:
                return "Customer updated successfully.";
            case 3320:
                return "Customer retrieved successfully.";
            case 341:
                return "Shopping cart for this customer not found.";
            case 3400:
                return "Order placed successfully.";
            case 3410:
                return "Orders retrieved successfully.";
        }
        return null;
    }

    public static boolean isValidEmail(String email) {

        String[] components1 = email.split("@", 0);
        if (components1.length != 2 | components1[0].length() == 0)
            return false;
        String[] components2 = components1[1].split("\\.", 0);
        if (components2.length < 2) {
            return false;
        } else {
            if (components2[0].length() == 0 | components2[1].length() == 0)
                return false;
        }

        return true;
    }

    public static CartInsertUpdateReqModel[] getCartItems(ResultSet rs) {
        ArrayList<CartInsertUpdateReqModel> list = new ArrayList<>();
        try {
            if (!rs.next()) {
                return null;
            } else {
                rs.previous();
            }
            while (rs.next()) {
                CartInsertUpdateReqModel item = new CartInsertUpdateReqModel(
                        rs.getString("email"),
                        rs.getString("movieId"),
                        rs.getInt("quantity")
                );
                list.add(item);
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }

        if (list.size() == 0)
            return null;

        CartInsertUpdateReqModel[] items = new CartInsertUpdateReqModel[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            items[i] = list.get(i);
        }
        return items;
    }

    public static ItemModel[] getSaleItems(ResultSet rs) {
        ArrayList<ItemModel> list = new ArrayList<>();
        try {
            if (!rs.next()) {
                return null;
            } else {
                rs.previous();
            }
            while (rs.next()) {
                ItemModel item = new ItemModel(
                        rs.getString("email"),
                        rs.getString("movieId"),
                        rs.getInt("quantity"),
                        new Date(rs.getDate("saleDate").getTime())
                );
                list.add(item);
            }

        } catch (SQLException e) {
            ServiceLogger.LOGGER.warning(ExceptionUtils.exceptionStackTraceAsString(e));
        }

        if (list.size() == 0)
            return null;

        ItemModel[] items = new ItemModel[list.size()];
        for (int i = 0; i < list.size(); ++i) {
            items[i] = list.get(i);
        }
        return items;
    }
}
