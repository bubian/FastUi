package com.pds.fast.ui.common.project;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;
import com.pds.fast.ui.common.FastDividerItemDecoration;
import com.pds.fast.ui.common.R;
import com.pds.fast.ui.common.Shapes;
import com.pds.fast.ui.common.assist.WarpClickListener;
import com.pds.fast.ui.common.dialog.FastBottomDialog;
import com.pds.fast.ui.common.dialog.FastDialog;

import java.util.ArrayList;
import java.util.List;

public class TaoWorkBottomDialog extends FastBottomDialog {

    private final int[] COLORS = {Color.parseColor("#FA6C00"),
            Color.parseColor("#3377FF"),
            Color.parseColor("#FAAF01"),
            Color.parseColor("#FA46A0"),
            Color.parseColor("#8F51FC")};

    private View rootView;
    private TextView startTaoWork;
    private RecyclerView selectedRecyclerView;
    private RecyclerView recyclerView;
    private ImageView ivChange;
    private static final int MAX_TAGS = 5;

    private static final String TAO_WORK_TEXT = "淘相似歌曲";
    private static final String CUSTOM_TAG = "自定义";
    // 搜索类型，和后端已经约定
    private static final String CUSTOM_TYPE = "search_word";
    protected final List<TaoWorkTagModel> selectedTags = new ArrayList<>();
    private int maxTags = MAX_TAGS;
    private int[] selectedTagsBgColors = COLORS;
    private String customTagType = CUSTOM_TYPE;
    private String taoWorkText = TAO_WORK_TEXT;
    private final TaoWorkTagModel CUSTOM = new TaoWorkTagModel(CUSTOM_TAG);

    private int contentViewHeight;
    protected boolean isImmediatelyCloseDialog = true;
    protected IAction action;
    private int indexOffset;

    public TaoWorkBottomDialog() {
        selectedTags.add(CUSTOM);
    }

    public void setAction(IAction action) {
        this.action = action;
    }

    public void setImmediatelyCloseDialog(boolean immediatelyCloseDialog) {
        isImmediatelyCloseDialog = immediatelyCloseDialog;
    }

    public TaoWorkBottomDialog setMaxTags(int maxTags) {
        this.maxTags = maxTags;
        return this;
    }

    public TaoWorkBottomDialog setSelectedTagsBgColors(int[] selectedTagsBgColors) {
        this.selectedTagsBgColors = selectedTagsBgColors;
        return this;
    }

    public TaoWorkBottomDialog setCustomTagType(String customTagType) {
        this.customTagType = customTagType;
        return this;
    }

    public TaoWorkBottomDialog setTaoWorkText(String taoWorkText) {
        this.taoWorkText = taoWorkText;
        return this;
    }

    public void setContentViewHeight(int contentViewHeight) {
        this.contentViewHeight = contentViewHeight;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        stopSwitchAnimation();
    }

//    public void hide(){
//        stopSwitchAnimation();
//        if (!isAdded()){
//            return;
//        }
//        FragmentManager manager = getFragmentManager();
//        if (null != manager){
//            manager.beginTransaction().hide(this).commitAllowingStateLoss();
//            getDialog().hide();
//        }
//    }
//
//    public void showFragment(){
//        if (!isAdded()){
//            return;
//        }
//        FragmentManager manager = getFragmentManager();
//        if (null != manager){
//            manager.beginTransaction().show(this).commitAllowingStateLoss();
//        }
//    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = super.onCreateView(inflater, container, savedInstanceState);
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.dialog_bottom_tao_work, container, false);
        }
        View contentView = rootView.findViewById(R.id.content);
        contentView.setBackground(new Shapes.Builder(Shapes.RECTANGLE)
                .setSolid(Color.WHITE)
                .setCornerRadii(dip2px(20), dip2px(20), 0, 0)
                .build());

        if (contentViewHeight > 0) {
            ViewGroup.LayoutParams params = contentView.getLayoutParams();
            if (null == params) {
                params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(410));
            }
            params.height = contentViewHeight;
            contentView.setLayoutParams(params);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        startTaoWork = view.findViewById(R.id.start_tao);
        startTaoWork.setText(taoWorkText);
        changeTaoWorkState();
        startTaoWork.setOnClickListener(new WarpClickListener() {
            @Override
            protected void onSingleClick(View v) {
                if (selectedTags.size() <= 1) {
                    return;
                }
                doStartTaoWork(v, selectedTags);
            }
        });

        initSelectedRecyclerView(view);
        view.findViewById(R.id.close).setOnClickListener(v -> {
            dismissAllowingStateLoss();
            clickClose();
        });
        view.findViewById(R.id.change).setOnClickListener(changeListener);
        ivChange = view.findViewById(R.id.iv_change);
        ivChange.setOnClickListener(changeListener);
        initSelectRecyclerView(view);
    }

    protected void doStartTaoWork(View view, List<TaoWorkTagModel> selectedTags) {
        if (null != action) {
            action.doTaoWork(selectedTags);
        }
        if (isImmediatelyCloseDialog) {
            dismissAllowingStateLoss();
        }
    }

    private void changeTaoWorkState() {
        int bgColor = selectedTags.size() > 1 ? getResources().getColor(R.color.color_FA3123) : getResources().getColor(R.color.color_99FA3123);
        startTaoWork.setBackground(new Shapes.Builder(Shapes.RECTANGLE)
                .setSolid(bgColor)
                .setCornerRadius(dip2px(23))
                .build());
    }

    private final WarpClickListener changeListener = new WarpClickListener() {
        @Override
        protected void onSingleClick(View v) {
            doChangeTagList(v);
        }
    };

    /****************************************************已选择标签***********************************/

    /**
     * 初始化已选择标签RecyclerView
     *
     * @param view
     */
    private boolean isInitSelectedTags = false;

    private void initSelectedRecyclerView(View view) {
        selectedRecyclerView = view.findViewById(R.id.selected_tags);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setReverseLayout(true);
        manager.setOrientation(RecyclerView.HORIZONTAL);
        selectedRecyclerView.setLayoutManager(manager);
        selectedRecyclerView.addItemDecoration(new FastDividerItemDecoration(view.getContext(),
                FastDividerItemDecoration.VERTICAL_LIST, dip2px(8), Color.TRANSPARENT));
        selectedRecyclerView.setAdapter(selectedTagsAdapter);
        selectedTagsAdapter.submitList(selectedTags);
        isInitSelectedTags = true;
    }

    public TaoWorkBottomDialog setSelectedTags(List<TaoWorkTagModel> models) {
        selectedTags.clear();
        selectedTags.add(CUSTOM);
        if (null != models) {
            selectedTags.addAll(models);
        }
        if (isInitSelectedTags) {
            selectedTagsAdapter.submitList(selectedTags);
        }
        return this;
    }

    private final ListAdapter<TaoWorkTagModel, SelectedTagsTagsHolder> selectedTagsAdapter =
            new ListAdapter<TaoWorkTagModel, SelectedTagsTagsHolder>(new DiffUtil.ItemCallback<TaoWorkTagModel>() {
                @Override
                public boolean areItemsTheSame(@NonNull TaoWorkTagModel oldItem, @NonNull TaoWorkTagModel newItem) {
                    return TextUtils.equals(oldItem.word, newItem.word) && TextUtils.equals(oldItem.type, newItem.type);
                }

                @Override
                public boolean areContentsTheSame(@NonNull TaoWorkTagModel oldItem, @NonNull TaoWorkTagModel newItem) {
                    return TextUtils.equals(oldItem.word, newItem.word);
                }
            }) {

                @NonNull
                @Override
                public SelectedTagsTagsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                    return new SelectedTagsTagsHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tags, parent, false));
                }

                @Override
                public void onBindViewHolder(@NonNull SelectedTagsTagsHolder holder, int position) {
                    holder.onBindHolder(getItem(position));
                }
            };

    private class SelectedTagsTagsHolder extends RecyclerView.ViewHolder {
        private final TextView tag;
        private final ImageView action;
        private final View view;

        public SelectedTagsTagsHolder(@NonNull View view) {
            super(view);
            this.view = view;
            tag = view.findViewById(R.id.tag);
            action = view.findViewById(R.id.action);

        }

        public void onBindHolder(@NonNull TaoWorkTagModel model) {
            boolean isCustom = CUSTOM_TAG.equals(model.getWord());
            int index = selectedTagsAdapter.getCurrentList().indexOf(model);
            int viewBgColor = isCustom ? view.getContext().getResources().getColor(R.color.color_14FA3123) : getSelectedTagBgColor(index);
            view.setBackground(new Shapes.Builder(Shapes.RECTANGLE)
                    .setSolid(viewBgColor)
                    .setCornerRadius(dip2px(16))
                    .build());
            tag.setText(model.getWord());

            int id = isCustom ? R.mipmap.ic_add_red : R.mipmap.ic_close_white;
            int textColor = view.getContext().getResources().getColor(isCustom ? R.color.color_FA3123 : R.color.color_FFFFFF);
            tag.setTextColor(textColor);

            action.setImageResource(id);
            action.setTag(model);
            addAndRemoveCustomTag(action);
        }
    }

    private void addAndRemoveCustomTag(View action) {
        action.setOnClickListener(v -> {
            Object object = action.getTag();
            if (object instanceof TaoWorkTagModel) {
                TaoWorkTagModel tagModel = (TaoWorkTagModel) object;
                boolean isCustom = CUSTOM_TAG.equals(tagModel.getWord());
                if (isCustom) {
                    doAddTagDialog(v.getContext());
                    return;
                }
                syncSelectedTags(tagModel, false);
                tagModel.setChecked(false);
                syncTagsAdapter(tagModel);
            }
        });
    }

    private void syncSelectedTags(TaoWorkTagModel tagModel, boolean isAdd) {
        if (tagModel == null) {
            return;
        }
        boolean lastSelectedTagsState = selectedTags.size() > 1;
        if (isAdd) {
            int addIndex = 1;
            selectedTags.add(addIndex, tagModel);
            selectedRecyclerView.scrollToPosition(0);
            selectedTagsAdapter.notifyItemInserted(addIndex);
            if (indexOffset >= selectedTagsBgColors.length) {
                indexOffset = 1;
            }
        } else {
            int index = selectedTags.indexOf(tagModel);
            if (index >= 0 && index < selectedTags.size()) {
                indexOffset++;
                selectedTags.remove(index);
                selectedTagsAdapter.notifyItemRemoved(index);
            }
        }
        if (lastSelectedTagsState != (selectedTags.size() > 1)) {
            changeTaoWorkState();
        }
    }

    private FastDialog addTagDialog;

    protected void doAddTagDialog(Context context) {
        if (null != addTagDialog && addTagDialog.isShowing()) {
            return;
        }
        addTagDialog = new FastDialog(context) {
            @Override
            protected void onCreate(Bundle savedInstanceState) {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.dialog_add_tag);
                TextView cancel = findViewById(R.id.cancel);
                cancel.setBackground(new Shapes.Builder(Shapes.RECTANGLE)
                        .setCornerRadius(dip2px(25))
                        .setSolid(context.getResources().getColor(R.color.color_F7F8FA))
                        .build());

                EditText editText = findViewById(R.id.et_tag);
                editText.setBackground(new Shapes.Builder(Shapes.RECTANGLE)
                        .setCornerRadius(dip2px(6))
                        .setSolid(context.getResources().getColor(R.color.color_F7F8FA))
                        .build());
                TextView add = findViewById(R.id.add);

                add.setBackground(new Shapes.Builder(Shapes.RECTANGLE)
                        .setCornerRadius(dip2px(25))
                        .setSolid(context.getResources().getColor(R.color.color_FA3123))
                        .build());

                add.setOnClickListener(new WarpClickListener() {
                    @Override
                    protected void onSingleClick(View v) {
                        String tag = editText.getText().toString();
                        if (TextUtils.isEmpty(tag)) {
                            Toast.makeText(context, context.getResources().getString(R.string.toast_unfilled_content), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (selectedTags.size() > maxTags) {
                            Toast.makeText(context, String.format(context.getResources().getString(R.string.toast_tag_upper_limit_five), maxTags), Toast.LENGTH_SHORT).show();
                            return;
                        }

                        TaoWorkTagModel newModel = new TaoWorkTagModel(tag, customTagType);
                        newModel.setChecked(true);
                        if (selectedTags.contains(newModel)) {
                            Toast.makeText(context, context.getResources().getString(R.string.toast_added_tag), Toast.LENGTH_SHORT).show();
                            return;
                        }
                        syncSelectedTags(newModel, true);
                        syncTagsAdapter(newModel);
                        dismiss();
                        clickAddCustomTag(tag);
                    }
                });
                cancel.setOnClickListener(v1 -> {
                    clickCancelCustomTagDialog(editText.getText().toString());
                    addTagDialog.dismiss();
                });
            }

        };
        addTagDialog.setCancelable(false);
        addTagDialog.show();
    }

    /**
     * 如果用户自定义文案下面已经存在，需要更改下面状态
     *
     * @param newModel
     */
    private void syncTagsAdapter(TaoWorkTagModel newModel) {
        List<TaoWorkTagModel> tagModels = tagsAdapter.getCurrentList();
        if (tagModels.contains(newModel)) {
            int tagIndex = tagModels.indexOf(newModel);
            tagModels.get(tagIndex).setChecked(newModel.isChecked());
            tagsAdapter.notifyItemChanged(tagIndex);
        }
    }

    private int getSelectedTagBgColor(int index) {
        return selectedTagsBgColors[calculateSelectedTagBgIndex(index)];
    }

    private int calculateSelectedTagBgIndex(int index) {
        int ind = Math.abs(selectedTags.size() - index + indexOffset);
        int colorSize = selectedTagsBgColors.length;
        return ind % colorSize;
    }


    /****************************************************选择你喜欢的标签***********************************/

    private List<TaoWorkTagModel> linkTags;
    private boolean isInitLikeTags = false;

    public TaoWorkBottomDialog setLikeTags(List<TaoWorkTagModel> models) {
        stopSwitchAnimation();
        if (null == models && null != linkTags) {
            linkTags.clear();
        }
        linkTags = models;
        if (isInitLikeTags) {
            tagsAdapter.submitList(linkTags);
        }
        return this;
    }

    private void initSelectRecyclerView(View view) {
        recyclerView = view.findViewById(R.id.like_tags);
        FlexboxLayoutManager manager = new FlexboxLayoutManager(view.getContext()) {
            @Override
            public RecyclerView.LayoutParams generateLayoutParams(ViewGroup.LayoutParams lp) {
                if (lp instanceof RecyclerView.LayoutParams) {
                    return new LayoutParams((RecyclerView.LayoutParams) lp);
                } else if (lp instanceof ViewGroup.MarginLayoutParams) {
                    return new LayoutParams((ViewGroup.MarginLayoutParams) lp);
                } else {
                    return new LayoutParams(lp);
                }
            }
        };
        manager.setFlexDirection(FlexDirection.ROW);
        manager.setAlignItems(AlignItems.STRETCH);
        manager.setJustifyContent(JustifyContent.FLEX_START);
        recyclerView.setLayoutManager(manager);
        recyclerView.addItemDecoration(new FastDividerItemDecoration(view.getContext(),
                FastDividerItemDecoration.BOTH_SET, dip2px(8), Color.TRANSPARENT).setDividerBoth(dip2px(8), dip2px(12)));
        recyclerView.setAdapter(tagsAdapter);
        tagsAdapter.submitList(linkTags);
        isInitLikeTags = true;
    }


    private void startSwitchAnimation(ImageView imageView) {
        if (null != ivChange) {
            ivChange.clearAnimation();
        }
        ivChange = imageView;
        RotateAnimation rotate = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        LinearInterpolator lin = new LinearInterpolator();
        rotate.setInterpolator(lin);
        rotate.setDuration(750);
        rotate.setRepeatCount(-1);
        rotate.setFillAfter(true);
        rotate.setStartOffset(10);
        ivChange.setAnimation(rotate);
    }

    private void stopSwitchAnimation() {
        if (null != ivChange) {
            ivChange.clearAnimation();
        }
    }

    private void doChangeTagList(View view) {
        stopSwitchAnimation();
        startSwitchAnimation(ivChange);
        doChangeTagApi(view);
    }

    protected void doChangeTagApi(View view) {
        if (null != action) {
            action.tagChange();
        }
    }

    private final ListAdapter<TaoWorkTagModel, TagsHolder> tagsAdapter
            = new ListAdapter<TaoWorkTagModel, TagsHolder>(new DiffUtil.ItemCallback<TaoWorkTagModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull TaoWorkTagModel oldItem, @NonNull TaoWorkTagModel newItem) {
            return TextUtils.equals(oldItem.word, newItem.word) && TextUtils.equals(oldItem.type, newItem.type);
        }

        @Override
        public boolean areContentsTheSame(@NonNull TaoWorkTagModel oldItem, @NonNull TaoWorkTagModel newItem) {
            return TextUtils.equals(oldItem.word, newItem.word);
        }
    }) {
        private View buildItemView(ViewGroup viewGroup) {
            Context context = viewGroup.getContext();
            TextView textView = new TextView(context);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, dip2px(32));
            textView.setLayoutParams(params);
            textView.setGravity(Gravity.CENTER);
            textView.setBackground(new Shapes.Builder(Shapes.RECTANGLE)
                    .setSolid(context.getResources().getColor(R.color.color_F7F8FA))
                    .setCornerRadius(dip2px(16))
                    .build());
            textView.setTextColor(context.getResources().getColor(R.color.color_1A1A1A));
            textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textView.setPadding(dip2px(16), 0, dip2px(16), 0);
            return textView;
        }

        @NonNull
        @Override
        public TagsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TagsHolder(buildItemView(parent));
        }

        @Override
        public void onBindViewHolder(@NonNull TagsHolder holder, int position) {
            holder.onBindHolder(getItem(position));
        }

        @Override
        public int getItemCount() {
            return null == linkTags ? 0 : linkTags.size();
        }
    };

    private class TagsHolder extends RecyclerView.ViewHolder {

        private final TextView textView;

        public TagsHolder(@NonNull View view) {
            super(view);
            this.textView = (TextView) view;

        }

        public void onBindHolder(@NonNull TaoWorkTagModel data) {
            Context context = textView.getContext();
            textView.setText(data.getWord());
            int tvColor = ContextCompat.getColor(context, data.isChecked() ? R.color.color_A6A6A6 : R.color.color_333333);
            textView.setTextColor(tvColor);
            textView.setTag(data);
            textView.setOnClickListener(tags);
        }
    }

    private final View.OnClickListener tags = v -> {
        Context context = v.getContext();
        Object object = v.getTag();
        if (object instanceof TaoWorkTagModel) {
            TaoWorkTagModel model = (TaoWorkTagModel) object;
            if (model.isChecked()) {
                return;
            }
            if (selectedTags.size() > maxTags) {
                Toast.makeText(context, String.format(context.getResources().getString(R.string.toast_tag_upper_limit_five), maxTags), Toast.LENGTH_SHORT).show();
                return;
            }
            model.setChecked(true);
            if (v instanceof TextView) {
                int tvColor = ContextCompat.getColor(context, model.isChecked() ? R.color.color_A6A6A6 : R.color.color_333333);
                ((TextView) v).setTextColor(tvColor);
            }
            syncSelectedTags(new TaoWorkTagModel(model.getWord(), model.getType(), true), true);
        }
    };

    public static int dip2px(float dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density + 0.5F);
    }

    public static class TaoWorkTagModel {
        public TaoWorkTagModel(String word) {
            this.word = word;
        }

        public TaoWorkTagModel(String word, String type) {
            this.type = type;
            this.word = word;
        }

        public TaoWorkTagModel(String word, String type, boolean isChecked) {
            this.type = type;
            this.word = word;
            this.isChecked = isChecked;
        }

        transient private boolean isChecked;
        private String type;
        private String word;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        @Override
        public boolean equals(Object o) {
            TaoWorkTagModel model = (TaoWorkTagModel) o;
            return TextUtils.equals(word, model.word);
        }
    }

    public interface IAction {
        default void tagChange() {
        }

        default void doTaoWork(List<TaoWorkTagModel> models) {
        }
    }

    /****************************************************点击事件回调，用于处理单独业务***********************************/
    protected void clickClose() {
    }

    protected void clickCancelCustomTagDialog(String tag) {

    }

    protected void clickAddCustomTag(String tag) {

    }
}
