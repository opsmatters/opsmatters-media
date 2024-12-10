package com.opsmatters.media.client.paypal;

import org.json.JSONObject;

/**
 * Represents the item object for a PayPal invoice.
 */
public class Item extends JSONObject
{
    private static final String NAME = "name";
    private static final String DESCRIPTION = "description";
    private static final String QUANTITY = "quantity";
    private static final String UNIT_AMOUNT = "unit_amount";
    private static final String TAX = "tax";
    private static final String DISCOUNT = "discount";
    private static final String UNIT_OF_MEASURE = "unit_of_measure";

    private UnitAmount unitAmount;
    private Tax tax;
    private Discount discount;

    /**
     * Default constructor.
     */
    public Item() 
    {
    }

    /**
     * Constructor that takes a JSONObject.
     */
    public Item(JSONObject obj) 
    {
        if(obj.has(NAME))
            setName(obj.optString(NAME));
        if(obj.has(DESCRIPTION))
            setDescription(obj.optString(DESCRIPTION));
        if(obj.has(QUANTITY))
            setQuantity(obj.optString(QUANTITY));
        if(obj.has(UNIT_OF_MEASURE))
            setUnitOfMeasure(obj.optString(UNIT_OF_MEASURE));

        if(obj.has(UNIT_AMOUNT))
            setUnitAmount(new UnitAmount(obj.getJSONObject(UNIT_AMOUNT)));
        if(obj.has(TAX))
            setTax(new Tax(obj.getJSONObject(TAX)));
        if(obj.has(DISCOUNT))
            setDiscount(new Discount(obj.getJSONObject(DISCOUNT)));
    }

    /**
     * Returns the "unit_amount" object.
     */
    public UnitAmount getUnitAmount()
    {
        if(unitAmount == null)
            setUnitAmount(new UnitAmount());
        return unitAmount;
    }

    /**
     * Sets the "unit_amount" object for the invoice.
     */
    public void setUnitAmount(UnitAmount unitAmount)
    {
        this.unitAmount = unitAmount;
        put(UNIT_AMOUNT, unitAmount);
    }

    /**
     * Returns the "tax" object.
     */
    public Tax getTax()
    {
        if(tax == null)
            setTax(new Tax());
        return tax;
    }

    /**
     * Sets the "tax" object for the invoice.
     */
    public void setTax(Tax tax)
    {
        this.tax = tax;
        put(TAX, tax);
    }

    /**
     * Returns the "discount" object.
     */
    public Discount getDiscount()
    {
        if(discount == null)
            setDiscount(new Discount());
        return discount;
    }

    /**
     * Sets the "discount" object for the invoice.
     */
    public void setDiscount(Discount discount)
    {
        this.discount = discount;
        put(DISCOUNT, discount);
    }

    /**
     * Returns the name.
     */
    public String getName() 
    {
        return optString(NAME);
    }

    /**
     * Sets the name.
     */
    public void setName(String name) 
    {
        put(NAME, name);
    }

    /**
     * Returns the description.
     */
    public String getDescription() 
    {
        return optString(DESCRIPTION);
    }

    /**
     * Sets the description.
     */
    public void setDescription(String description) 
    {
        put(DESCRIPTION, description);
    }

    /**
     * Returns the quantity.
     */
    public String getQuantity() 
    {
        return optString(QUANTITY);
    }

    /**
     * Sets the quantity.
     */
    public void setQuantity(String quantity) 
    {
        put(QUANTITY, quantity);
    }

    /**
     * Returns the unit of measure.
     */
    public String getUnitOfMeasure() 
    {
        return optString(UNIT_OF_MEASURE);
    }

    /**
     * Sets the unit of measure.
     */
    public void setUnitOfMeasure(String unitOfMeasure) 
    {
        put(UNIT_OF_MEASURE, unitOfMeasure);
    }
}