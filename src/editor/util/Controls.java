package editor.util;

import dream.graphics.icon.Icons;
import dream.managers.ResourcePool;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiColorEditFlags;
import imgui.type.ImFloat;
import imgui.type.ImInt;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class Controls
{
    private static float dragIncrement = 0.1f;
    private static float indentation = 2.0f;

    public static final float btnSize = 20.0f;
    public static final float inSize = 40.0f;
    public static final float pad = 10.0f;

    private static String formatFloatString = "%.2f";


    public static float getIndentation()
    {
        return Controls.indentation;
    }

    public static void setIndentation(float indentation)
    {
        Controls.indentation = indentation;
    }

    public static void setDragIncrement(float dragIncrement)
    {
        Controls.dragIncrement = dragIncrement;
    }

    public static void setFormatFloatString(String format)
    {
        Controls.formatFloatString = format;
    }

    public static void resetIncrement()
    {
        Controls.dragIncrement = 0.1f;
    }

    public static void resetFormat()
    {
        Controls.formatFloatString = "%.3f";
    }

    public static boolean drawVector3Control(Vector3f values, String label)
    {
        String ID = label + values.hashCode();

        ImGui.pushStyleColor(ImGuiCol.Button, 0.6f, 0.1f, 0.1f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.8f, 0.1f, 0.1f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 1.0f, 0.1f, 0.1f, 1.0f);
        ImGui.pushItemWidth(btnSize);
        ImGui.button("X");
        ImGui.popItemWidth();
        ImGui.popStyleColor(3);
        ImGui.sameLine(btnSize + pad);
        float[] arr = new float[] {values.x};
        ImGui.pushItemWidth(inSize);
        boolean change = ImGui.dragFloat(ID +"X", arr, Controls.dragIncrement,
                -Float.MAX_VALUE, Float.MAX_VALUE, Controls.formatFloatString);
        values.x = arr[0];
        ImGui.popItemWidth();

        ImGui.sameLine(btnSize + inSize + (pad * 3));

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.6f, 0.1f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.1f, 0.8f, 0.1f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 1.0f, 0.1f, 1.0f);
        ImGui.pushItemWidth(btnSize);
        ImGui.button("Y");
        ImGui.popItemWidth();
        ImGui.popStyleColor(3);
        ImGui.sameLine((btnSize * 2) + inSize + (pad * 4));
        arr[0] = values.y;
        ImGui.pushItemWidth(inSize);
        change = change || ImGui.dragFloat(ID + "Y", arr, Controls.dragIncrement,
                -Float.MAX_VALUE, Float.MAX_VALUE, Controls.formatFloatString);
        values.y = arr[0];
        ImGui.popItemWidth();

        ImGui.sameLine((btnSize * 2) + (inSize * 2) + (pad * 6));

        ImGui.pushStyleColor(ImGuiCol.Button, 0.1f, 0.1f, 0.6f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0.1f, 0.1f, 0.8f, 1.0f);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0.1f, 0.1f, 1.0f, 1.0f);
        ImGui.pushItemWidth(btnSize);
        ImGui.button("Z");
        ImGui.popItemWidth();
        ImGui.popStyleColor(3);
        ImGui.sameLine((btnSize * 3) + (inSize * 2) + (pad * 7));
        arr[0] = values.z;
        ImGui.pushItemWidth(inSize);
        change = change || ImGui.dragFloat(ID + "Z", arr, Controls.dragIncrement,
                -Float.MAX_VALUE, Float.MAX_VALUE, Controls.formatFloatString);
        values.z = arr[0];
        ImGui.popItemWidth();

        return change;
    }

    public static boolean drawVector3Control(float[] valuesArray, String label)
    {
        return ImGui.dragFloat3(label, valuesArray, Controls.dragIncrement,
                -Float.MAX_VALUE, Float.MAX_VALUE, Controls.formatFloatString);
    }

    public static boolean drawVector3Control(String label, Vector3f values)
    {
        float[] valuesArray = new float[] {values.x, values.y, values.z};

        boolean res = ImGui.dragFloat3(label, valuesArray, Controls.dragIncrement,
                -Float.MAX_VALUE, Float.MAX_VALUE, Controls.formatFloatString);

        if(res)
        {
            values.x = valuesArray[0];
            values.y = valuesArray[1];
            values.z = valuesArray[2];
        }

        return res;
    }

    public static boolean drawVector2Control(Vector2f values, String label)
    {
        float[] valuesArray = new float[] {values.x, values.y};

        boolean res = ImGui.dragFloat2(label, valuesArray, Controls.dragIncrement,
                -Float.MAX_VALUE, Float.MAX_VALUE, Controls.formatFloatString);

        if(res)
        {
            values.x = valuesArray[0];
            values.y = valuesArray[1];
        }

        return res;
    }

    public static boolean drawVector4Control(Vector4f values, String label)
    {
        float[] valuesArray = new float[] {values.x, values.y, values.z, values.w};

        boolean res = ImGui.dragFloat4(label, valuesArray, Controls.dragIncrement,
                -Float.MAX_VALUE, Float.MAX_VALUE, Controls.formatFloatString);

        if(res)
        {
            values.x = valuesArray[0];
            values.y = valuesArray[1];
            values.z = valuesArray[2];
            values.w = valuesArray[3];
        }

        return res;
    }

    public static boolean dragInt(String label, int[] value)
    {
        ImGui.text(label + ":");
        ImGui.sameLine();
        ImGui.pushItemWidth(85.0f);
        boolean change = ImGui.dragInt("##" + label, value, 1, -Integer.MAX_VALUE, Integer.MAX_VALUE);
        ImGui.popItemWidth();
        return change;
    }

    public static boolean inputInt(String label, int[] value)
    {
        ImGui.text(label + ":");
        ImGui.sameLine();
        ImGui.pushItemWidth(85.0f);
        ImInt imInt = new ImInt(value[0]);
        boolean change = ImGui.inputInt("##" + label, imInt, 1);
        ImGui.popItemWidth();
        value[0] = imInt.get();
        return change;
    }

    public static boolean inputFloat(String label, float[] value)
    {
        ImGui.text(label + ":");
        ImGui.sameLine();
        ImGui.pushItemWidth(85.0f);
        ImFloat imFloat = new ImFloat(value[0]);
        boolean change = ImGui.inputFloat("##" + label, new ImFloat(value[0]));
        value[0] = imFloat.get();
        ImGui.popItemWidth();
        return change;
    }

    public static boolean dragFloat(String label, float value)
    {
        return dragFloat(label, new float[]{value});
    }

    public static boolean dragFloat(String label, float[] value)
    {
        ImGui.text(label + ":");
        ImGui.sameLine();
        ImGui.pushItemWidth(85.0f);
        boolean change = ImGui.dragFloat("##" + label, value, Controls.dragIncrement);
        ImGui.popItemWidth();
        return change;
    }

    public static boolean dragFloat(float[] value)
    {
        ImGui.pushItemWidth(Controls.inSize);
        boolean change = ImGui.dragFloat("##val", value, 0);
        ImGui.popItemWidth();
        return change;
    }

    public static boolean drawBooleanControl(boolean value, String label)
    {
        boolean res = value;
        ImGui.text(label + ":");
        ImGui.sameLine();
        if(ImGui.checkbox("##"+label, value))
            res = !value;
        return res;
    }

    public static void drawHelpMarker(String description)
    {
        ImGui.pushStyleColor(ImGuiCol.Button, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonHovered, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.ButtonActive, 0, 0, 0, 0);
        ImGui.pushStyleColor(ImGuiCol.Border, 0, 0, 0, 0);
        ImGui.imageButton(ResourcePool.getIcon(Icons.about), 12.0f, 12.0f, 0, 1, 1, 0);
        if (ImGui.isItemHovered())
        {
            ImGui.beginTooltip();
            ImGui.pushTextWrapPos(ImGui.getFontSize() * 35.0f);
            ImGui.textUnformatted(description);
            ImGui.popTextWrapPos();
            ImGui.endTooltip();
        }
        ImGui.popStyleColor(4);
    }

    public static void colorPicker4(String label, Vector4f color)
    {
        float[] colors = {color.x, color.y, color.z, color.w};
        ImGui.text(label + ":");
        ImGui.sameLine();
        boolean change = ImGui.colorEdit4("##" + label, colors, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoLabel);
        if(change)
        {
            color.x = colors[0];
            color.y = colors[1];
            color.z = colors[2];
            color.w = colors[3];
        }
    }

    public static boolean colorPicker4(String label, float[] colors)
    {
        return ImGui.colorEdit4(label, colors, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoLabel);
    }

    public static boolean colorPicker3(String label, Vector3f color)
    {
        float[] colors = {color.x, color.y, color.z};
        boolean change = ImGui.colorEdit3(label, colors, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoLabel);
        if(change)
        {
            color.x = colors[0];
            color.y = colors[1];
            color.z = colors[2];
        }
        return change;
    }

    public static boolean colorPicker3(String label, float[] colors)
    {
        ImGui.text(label + ":");
        ImGui.sameLine();
        return ImGui.colorEdit3(label, colors, ImGuiColorEditFlags.NoInputs | ImGuiColorEditFlags.NoLabel);
    }
}

