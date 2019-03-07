package sk.pa3kc.html;

import java.util.ArrayList;
import java.util.List;

public class HtmlElement
{
    public final String name;
    public final boolean nested;
    public final HtmlElement nestedElement;
    public final List<String> attributeNames = new ArrayList<String>();
    public final List<String> attributeValues = new ArrayList<String>();

    public HtmlElement(String name)
    {
        this(name, false, null);
    }
    public HtmlElement(String name, boolean nested, HtmlElement nestElement)
    {
        this.name = name;
        this.nested = nested;
        this.nestedElement = nestElement;    
    }

    public void addAttribute(String name, String value)
    {
        this.attributeNames.add(name);
        this.attributeValues.add(value);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        builder.append('<');
        builder.append(this.name);
        builder.append(' ');

        if (this.nested == true)
        {
            builder.append(this.nestedElement.toString());
            builder.append("</");
            builder.append(this.name);
            builder.append('>');
        }

        return builder.toString();
    }
}