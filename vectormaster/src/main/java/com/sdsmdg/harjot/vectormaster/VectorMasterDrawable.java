package com.sdsmdg.harjot.vectormaster;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import com.sdsmdg.harjot.vectormaster.models.ClipPathModel;
import com.sdsmdg.harjot.vectormaster.models.GroupModel;
import com.sdsmdg.harjot.vectormaster.models.PathModel;
import com.sdsmdg.harjot.vectormaster.models.VectorModel;
import com.sdsmdg.harjot.vectormaster.utilities.Utils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.Stack;

public class VectorMasterDrawable extends Drawable {

    VectorModel vectorModel;
    Context context;

    Resources resources;
    int resID = -1;
    boolean useLegacyParser = true;

    XmlPullParser xpp;

    String TAG = "VECTOR_MASTER";

    private Matrix scaleMatrix;

    int width = -1, height = -1;

    private float scaleRatio, strokeRatio;

    private int left = 0, top = 0;
    private int tempSaveCount;

    public VectorMasterDrawable(Context context, int resID) {
        this.context = context;
        this.resID = resID;
        init();
    }

    void init() {
        resources = context.getResources();
        buildVectorModel();
    }

    void buildVectorModel() {

        if (resID == -1) {
            vectorModel = null;
            return;
        }

        xpp = resources.getXml(resID);

        int tempPosition;
        PathModel pathModel = new PathModel();
        vectorModel = new VectorModel();
        GroupModel groupModel = new GroupModel();
        ClipPathModel clipPathModel = new ClipPathModel();
        Stack<GroupModel> groupModelStack = new Stack<>();

        try {
            int event = xpp.getEventType();
            while (event != XmlPullParser.END_DOCUMENT) {
                String name = xpp.getName();
                switch (event) {
                    case XmlPullParser.START_TAG:
                        if (name.equals("vector")) {
                            tempPosition = getAttrPosition(xpp, "viewportWidth");
                            vectorModel.setViewportWidth((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.VECTOR_VIEWPORT_WIDTH);

                            tempPosition = getAttrPosition(xpp, "viewportHeight");
                            vectorModel.setViewportHeight((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.VECTOR_VIEWPORT_HEIGHT);

                            tempPosition = getAttrPosition(xpp, "alpha");
                            vectorModel.setAlpha((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.VECTOR_ALPHA);

                            tempPosition = getAttrPosition(xpp, "name");
                            vectorModel.setName((tempPosition != -1) ? xpp.getAttributeValue(tempPosition) : null);

                            tempPosition = getAttrPosition(xpp, "width");
                            vectorModel.setWidth((tempPosition != -1) ? Utils.getFloatFromDimensionString(xpp.getAttributeValue(tempPosition)) : DefaultValues.VECTOR_WIDTH);

                            tempPosition = getAttrPosition(xpp, "height");
                            vectorModel.setHeight((tempPosition != -1) ? Utils.getFloatFromDimensionString(xpp.getAttributeValue(tempPosition)) : DefaultValues.VECTOR_HEIGHT);
                        } else if (name.equals("path")) {
                            pathModel = new PathModel();

                            tempPosition = getAttrPosition(xpp, "name");
                            pathModel.setName((tempPosition != -1) ? xpp.getAttributeValue(tempPosition) : null);

                            tempPosition = getAttrPosition(xpp, "fillAlpha");
                            pathModel.setFillAlpha((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_FILL_ALPHA);

                            tempPosition = getAttrPosition(xpp, "fillColor");
                            pathModel.setFillColor((tempPosition != -1) ? Utils.getColorFromString(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_FILL_COLOR);

                            tempPosition = getAttrPosition(xpp, "fillType");
                            pathModel.setFillType((tempPosition != -1) ? Utils.getFillTypeFromString(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_FILL_TYPE);

                            tempPosition = getAttrPosition(xpp, "pathData");
                            pathModel.setPathData((tempPosition != -1) ? xpp.getAttributeValue(tempPosition) : null);

                            tempPosition = getAttrPosition(xpp, "strokeAlpha");
                            pathModel.setStrokeAlpha((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_STROKE_ALPHA);

                            tempPosition = getAttrPosition(xpp, "strokeColor");
                            pathModel.setStrokeColor((tempPosition != -1) ? Utils.getColorFromString(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_STROKE_COLOR);

                            tempPosition = getAttrPosition(xpp, "strokeLineCap");
                            pathModel.setStrokeLineCap((tempPosition != -1) ? Utils.getLineCapFromString(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_STROKE_LINE_CAP);

                            tempPosition = getAttrPosition(xpp, "strokeLineJoin");
                            pathModel.setStrokeLineJoin((tempPosition != -1) ? Utils.getLineJoinFromString(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_STROKE_LINE_JOIN);

                            tempPosition = getAttrPosition(xpp, "strokeMiterLimit");
                            pathModel.setStrokeMiterLimit((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_STROKE_MITER_LIMIT);

                            tempPosition = getAttrPosition(xpp, "strokeWidth");
                            pathModel.setStrokeWidth((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_STROKE_WIDTH);

                            tempPosition = getAttrPosition(xpp, "trimPathEnd");
                            pathModel.setTrimPathEnd((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_TRIM_PATH_END);

                            tempPosition = getAttrPosition(xpp, "trimPathOffset");
                            pathModel.setTrimPathOffset((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_TRIM_PATH_OFFSET);

                            tempPosition = getAttrPosition(xpp, "trimPathStart");
                            pathModel.setTrimPathStart((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.PATH_TRIM_PATH_START);

                            pathModel.buildPath(useLegacyParser);
                        } else if (name.equals("group")) {
                            groupModel = new GroupModel();

                            tempPosition = getAttrPosition(xpp, "name");
                            groupModel.setName((tempPosition != -1) ? xpp.getAttributeValue(tempPosition) : null);

                            tempPosition = getAttrPosition(xpp, "pivotX");
                            groupModel.setPivotX((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.GROUP_PIVOT_X);

                            tempPosition = getAttrPosition(xpp, "pivotY");
                            groupModel.setPivotY((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.GROUP_PIVOT_Y);

                            tempPosition = getAttrPosition(xpp, "rotation");
                            groupModel.setRotation((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.GROUP_ROTATION);

                            tempPosition = getAttrPosition(xpp, "scaleX");
                            groupModel.setScaleX((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.GROUP_SCALE_X);

                            tempPosition = getAttrPosition(xpp, "scaleY");
                            groupModel.setScaleY((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.GROUP_SCALE_Y);

                            tempPosition = getAttrPosition(xpp, "translateX");
                            groupModel.setTranslateX((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.GROUP_TRANSLATE_X);

                            tempPosition = getAttrPosition(xpp, "translateY");
                            groupModel.setTranslateY((tempPosition != -1) ? Float.parseFloat(xpp.getAttributeValue(tempPosition)) : DefaultValues.GROUP_TRANSLATE_Y);

                            groupModelStack.push(groupModel);
                        } else if (name.equals("clip-path")) {
                            clipPathModel = new ClipPathModel();

                            tempPosition = getAttrPosition(xpp, "name");
                            clipPathModel.setName((tempPosition != -1) ? xpp.getAttributeValue(tempPosition) : null);

                            tempPosition = getAttrPosition(xpp, "pathData");
                            clipPathModel.setPathData((tempPosition != -1) ? xpp.getAttributeValue(tempPosition) : null);

                            clipPathModel.buildPath(useLegacyParser);
                        }
                        break;

                    case XmlPullParser.END_TAG:
                        if (name.equals("path")) {
                            if (groupModelStack.size() == 0) {
                                vectorModel.addPathModel(pathModel);
                            } else {
                                groupModelStack.peek().addPathModel(pathModel);
                            }
                            vectorModel.getFullpath().addPath(pathModel.getPath());
                        } else if (name.equals("clip-path")) {
                            if (groupModelStack.size() == 0) {
                                vectorModel.addClipPathModel(clipPathModel);
                            } else {
                                groupModelStack.peek().addClipPathModel(clipPathModel);
                            }
                        } else if (name.equals("group")) {
                            GroupModel topGroupModel = groupModelStack.pop();
                            if (groupModelStack.size() == 0) {
                                topGroupModel.setParent(null);
                                vectorModel.addGroupModel(topGroupModel);
                            } else {
                                topGroupModel.setParent(groupModelStack.peek());
                                groupModelStack.peek().addGroupModel(topGroupModel);
                            }
                        } else if (name.equals("vector")) {
                            vectorModel.buildTransformMatrices();
                        }
                        break;
                }
                event = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }

    }

    int getAttrPosition(XmlPullParser xpp, String attrName) {
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            if (xpp.getAttributeName(i).equals(attrName)) {
                return i;
            }
        }
        return -1;
    }

    public int getResID() {
        return resID;
    }

    public void setResID(int resID) {
        this.resID = resID;
        buildVectorModel();
        scaleMatrix = null;
    }

    public boolean isUseLegacyParser() {
        return useLegacyParser;
    }

    public void setUseLegacyParser(boolean useLegacyParser) {
        this.useLegacyParser = useLegacyParser;
        buildVectorModel();
        scaleMatrix = null;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        if (bounds.width() != 0 && bounds.height() != 0) {

            left = bounds.left;
            top = bounds.top;

            width = bounds.width();
            height = bounds.height();

            buildScaleMatrix();
            scaleAllPaths();
            scaleAllStrokes();
        }
    }

    @Override
    public void draw(Canvas canvas) {

        if (vectorModel == null) {
            return;
        }

        if (scaleMatrix == null) {
            int temp1 = Utils.dpToPx((int) vectorModel.getWidth());
            int temp2 = Utils.dpToPx((int) vectorModel.getHeight());

            setBounds(0, 0, temp1, temp2);
        }

        setAlpha(Utils.getAlphaFromFloat(vectorModel.getAlpha()));

        if (left != 0 || top != 0) {
            tempSaveCount = canvas.save();
            canvas.translate(left, top);
            vectorModel.drawPaths(canvas);
            canvas.restoreToCount(tempSaveCount);
        } else {
            vectorModel.drawPaths(canvas);
        }

    }

    @Override
    public void setAlpha(int i) {
        vectorModel.setAlpha(Utils.getAlphaFromInt(i));
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {

    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    public int getIntrinsicWidth() {
        return Utils.dpToPx((int) vectorModel.getWidth());
    }

    @Override
    public int getIntrinsicHeight() {
        return Utils.dpToPx((int) vectorModel.getHeight());
    }

    void buildScaleMatrix() {
        scaleMatrix = new Matrix();

        scaleMatrix.postTranslate(width / 2 - vectorModel.getViewportWidth() / 2, height / 2 - vectorModel.getViewportHeight() / 2);

        float widthRatio = width / vectorModel.getViewportWidth();
        float heightRatio = height / vectorModel.getViewportHeight();
        float ratio = Math.min(widthRatio, heightRatio);

        scaleRatio = ratio;

        scaleMatrix.postScale(ratio, ratio, width / 2, height / 2);
    }

    void scaleAllPaths() {
        vectorModel.scaleAllPaths(scaleMatrix);
    }

    void scaleAllStrokes() {
        strokeRatio = Math.min(width / vectorModel.getWidth(), height / vectorModel.getHeight());
        vectorModel.scaleAllStrokeWidth(strokeRatio);
    }

    public Path getFullPath() {
        if (vectorModel != null) {
            return vectorModel.getFullpath();
        }
        return null;
    }
    
    /**
     * Return all {@link GroupModel} objects with the same name
     *
     * @return non-null array of matches
     */
    public ArrayList<GroupModel> getAllGroupModelsByName(String name) {
        ArrayList<GroupModel> gModels = new ArrayList<>();
        GroupModel gModel;
        for (GroupModel groupModel : vectorModel.getGroupModels()) {
            if (groupModel.getName() != null && groupModel.getName().equals(name)) {
                gModels.add(groupModel);
            } else {
                gModel = groupModel.getGroupModelByName(name);
                if (gModel != null)
                    gModels.add(gModel);
            }
        }
        return gModels;
    }

    public GroupModel getGroupModelByName(String name) {
        GroupModel gModel;
        for (GroupModel groupModel : vectorModel.getGroupModels()) {
            if (groupModel.getName() != null && groupModel.getName().equals(name)) {
                return groupModel;
            } else {
                gModel = groupModel.getGroupModelByName(name);
                if (gModel != null)
                    return gModel;
            }
        }
        return null;
    }

    /**
     * Return all {@link PathModel} objects with the same name
     *
     * @return non-null array of matches
     */
    @NonNull
    public ArrayList<PathModel> getAllPathModelsByName(String name) {
        ArrayList<PathModel> pModels = new ArrayList<>();
        PathModel pModel = null;

        for (PathModel pathModel : vectorModel.getPathModels()) {
            if (pathModel.getName() != null && pathModel.getName().equals(name)) {
                pModels.add(pathModel);
            }
        }
        for (GroupModel groupModel : vectorModel.getGroupModels()) {
            pModel = groupModel.getPathModelByName(name);
            if (pModel != null && pModel.getName() != null && pModel.getName().equals(name))
                pModels.add(pModel);
        }
        return pModels;
    }

    public PathModel getPathModelByName(String name) {
        PathModel pModel = null;
        for (PathModel pathModel : vectorModel.getPathModels()) {
            if (pathModel.getName() != null && pathModel.getName().equals(name)) {
                return pathModel;
            }
        }
        for (GroupModel groupModel : vectorModel.getGroupModels()) {
            pModel = groupModel.getPathModelByName(name);
            if (pModel != null && pModel.getName() != null && pModel.getName().equals(name))
                return pModel;
        }
        return pModel;
    }

    /**
     * Return all {@link ClipPathModel} objects with the same name
     *
     * @return non-null array of matches
     */
    @NonNull
    public ArrayList<ClipPathModel> getAllClipPathModelsByName(String name) {
        ArrayList<ClipPathModel> cModels = new ArrayList<>();
        ClipPathModel cModel = null;
        for (ClipPathModel clipPathModel : vectorModel.getClipPathModels()) {
            if (clipPathModel.getName() != null && clipPathModel.getName().equals(name)) {
                cModels.add(clipPathModel);
            }
        }
        for (GroupModel groupModel : vectorModel.getGroupModels()) {
            cModel = groupModel.getClipPathModelByName(name);
            if (cModel != null && cModel.getName() != null && cModel.getName().equals(name))
                cModels.add(cModel);
        }
        return cModels;
    }

    public ClipPathModel getClipPathModelByName(String name) {
        ClipPathModel cModel = null;
        for (ClipPathModel clipPathModel : vectorModel.getClipPathModels()) {
            if (clipPathModel.getName() != null && clipPathModel.getName().equals(name)) {
                return clipPathModel;
            }
        }
        for (GroupModel groupModel : vectorModel.getGroupModels()) {
            cModel = groupModel.getClipPathModelByName(name);
            if (cModel != null && cModel.getName() != null && cModel.getName().equals(name))
                return cModel;
        }
        return cModel;
    }

    public void update() {
        invalidateSelf();
    }

    public float getScaleRatio() {
        return scaleRatio;
    }

    public float getStrokeRatio() {
        return strokeRatio;
    }

    public Matrix getScaleMatrix() {
        return scaleMatrix;
    }
}
