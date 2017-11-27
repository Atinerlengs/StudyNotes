#### 黑名单
###### 黑名单介绍
黑名单顾名思义就是当我们把一个联系人或者一个号码拉入到黑名单之后,我们不再接受到该联系人
的电话以及短信了.在如今骚扰电话短信横行的时代,黑名单功能还是很有必要的.
###### 黑名单工作原理
黑名单的操作是通过com.android.providers.BlockNumberProvider管理,这个应用是
系统自带的,主要是用来管理黑名单数据库. 在来电的时候,都会先对电话短信进行isBlocked()
判断,此方法是在frameworks/opt/telephony/src/java/com/android/internal/telephony/BlockChecker.java
里面.如果isBlocked为false,则对应的电话短信会被拦截.
###### 如果添加至黑名单
    1. 通过手动输入号码
    2. 进入联系人里面选择添加
以上种方法有一个区别就是通过联系人里面添加的时候,会把联系人名称也添加进去
###### 黑名单数据库
     /data/user_de/0/com.android.providers.blockednumbers/databases/blocknumbers.db
     原生黑名单blocked表里面只有_id,original_number,e164_number.为了在黑名单列表里面添加黑名单
     名称,在blocked添加了original_name一列.
###### 详细介绍
下面主要是介绍一下添加至黑名单的流程
<br>
BlockedNumberActivity.java黑名单列表界面有一个添加电话号码选项,点击之后会弹出一个dialog,在里面我们
可以选择手动输入或者是从联系人里面添加.下面我们主要说的是从哦你联系人添加至黑名单
```
        ImageView imageview = (ImageView) dialogView.findViewById(R.id.add_blocked_number_iv);
        imageview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                dialog.dismiss();
                Intent contactPickerIntent = new Intent(ACTION_CONTACT_SELECTION);
                contactPickerIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
                startActivityForResult(contactPickerIntent,
                        CHOOSE_CONTACTS_BLOCKED_CODE);
            }
        });
```
点击进入ContactListMultiChoiceActivity.java,在这个里面会把所选择的联系人的_id通过array传回来
```
    private void setOptionAction(){
        if (mListFragment instanceof MultiDuplicationPickerFragment) {
            setResult(ContactImportExportActivity.RESULT_CODE);
        }
        if (mListFragment instanceof PhoneAndEmailsPickerFragment) {
            PhoneAndEmailsPickerFragment fragment =
                    (PhoneAndEmailsPickerFragment) mListFragment;
            fragment.setNumberBalance(mNumberBalance);
            fragment.onOptionAction();
        } else {
            mListFragment.onOptionAction();
        }
    }
    //这个方法是各个对应的fragment里面的
    public void onOptionAction() {
        //这个id就是Contacts2.db里面data表里面的id,每个号码都是对应一个单独的_id
        final long[] idArray = getCheckedItemIds();
        if (idArray == null) {
            Log.e(TAG, "[onOptionAction]idArray is null,return.");
            return;
        }

        final Activity activity = getActivity();
        final Intent retIntent = new Intent();
        //其它Activity通过onActivityResult方法去获取这个idArray
        retIntent.putExtra(RESULT_INTENT_EXTRA_NAME, idArray);
        activity.setResult(Activity.RESULT_OK, retIntent);
        activity.finish();
    }
```
在ContactsListMultiCHoiceActivity界面选择好了联系人点击确定之后,BlockedNumberActivity
里面onActivityResult()操作.
```
        if (requestCode == CHOOSE_CONTACTS_BLOCKED_CODE) {
            if (data != null) {
                long[] contactIds = data.getLongArrayExtra("com.mediatek.contacts.list.pickdataresult");
                ArrayList<Object> params = new ArrayList<Object>();
                params.add(contactIds);
                params.add(this);
                //获取数据之后
                addBlockedNumber(params);
            }
        }

    private void addBlockedNumber(ArrayList<Object> ids){
        mBlockNumberTaskFragment.blockIfNotAlreadyBlocked(ids, this);
    }

    //异步线程去添加
    public void blockIfNotAlreadyBlocked(ArrayList<Object> ids, Listener listener){
        mListener = listener;
        mTask = new BlockNumberTask();
        mTask.execute(ids);
    }

    private class BlockNumberTask extends AsyncTask<ArrayList, Void, Boolean> {
        private String blockedName;
        private String blockedNumber;

        @Override
        protected Boolean doInBackground(ArrayList... params) {
            if (params.length == 0) {
                return null;
            }
            ArrayList<Object> list = params[0];
            long[] ids = (long[]) list.get(0);
            StringBuilder idSetBuilder = new StringBuilder();
            boolean first = true;
            for (long id : ids) {
                if (first) {
                    first = false;
                    idSetBuilder.append(id);
                } else {
                    idSetBuilder.append(',').append(id);
                }
            }
            Cursor cursor = null;
            ContentResolver contentResolver = getContext().getContentResolver();
            if (idSetBuilder.length() > 0) {
            //通过_id查询contacts.db里面的data表 ,获取cursor方便后面去查询name和number
                final String whereClause = ContactsContract.CommonDataKinds.Phone._ID + " IN (" + idSetBuilder.toString() + ")";
                cursor = contentResolver.query(
                        PHONES_WITH_PRESENCE_URI, CALLER_ID_PROJECTION, whereClause, null, null);
            }
            if (cursor == null) {
                return false;
            }
            try {
                while (cursor.moveToNext()) {
                    //查询到blockedName 以及 blockedNumber
                    blockedName = cursor.getString(CONTACT_NAME_COLUMN);
                    blockedNumber = cursor.getString(PHONE_NUMBER_COLUMN);
                    if (BlockedNumberContract.isBlocked(getContext(), blockedNumber)) {
                        continue;
                    } else {
                        //把上面查询到的在put到黑名单数据库里面,到这一步就已经添加成功了
                        ContentValues newValues = new ContentValues();
                        newValues.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NUMBER,
                                blockedNumber);
                        newValues.put(BlockedNumberContract.BlockedNumbers.COLUMN_ORIGINAL_NAME,
                                blockedName);
                        contentResolver.insert(BlockedNumberContract.BlockedNumbers.CONTENT_URI,
                                newValues);
                    }
                }
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
            return true;
        }

        @Override
        protected void onPostExecute(Boolean result) {
            mTask = null;
            if (mListener != null) {
                mListener.onBlocked(blockedNumber, result /* alreadyBlocked */);
            }
            mListener = null;
        }
    }
```
###### 总结
感觉添加完这个多选添加之后,主要的内容是对数据库的操作,涉及到对contacts2.db里面data表的查询以及
对blockednumber.db里面blocked表再添加blockedname一列和常规的增,删,查询等操作.


