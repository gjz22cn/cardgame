package com.lordcard.ui;

//public class DeleteMessage {  
//    private PegaSocket2 deleteMessage;  
//    private int idelete;  
//    private Cursor eventCursor, remindersCursor;  
//    private int[] column = new int[1];  
//    private String[] threadid = new String[1];  
//    private Cursor querymessage;  
//    DeleteMessage(PegaSocket2 deleteMessage, int id) {  
//        this.deleteMessage = deleteMessage;  
//        idelete = id;  
//        String uriInbox = "content://sms";  
//        String[] projection = new String[] { "thread_id" };  
//        Uri uriSms = Uri.parse(uriInbox);  
//        String where = "_id = " + Integer.toString(idelete);  
//        querymessage = deleteMessage.getContentResolver().query(uriSms,  
//                projection, where, null, null);  
//    }  
//    private void MoveToFirst() {  
//        querymessage.moveToFirst();  
//    }  
//   
//    public int DeleteShortMessage() {  
//        if (querymessage.getCount() != 0) {  
//            MoveToFirst();  
//            threadid[0] = "thread_id";  
//            column[0] = querymessage.getColumnIndex("thread_id");  
//            threadid[0] = querymessage.getString(column[0]);  
//            // delete message  
//            int number = deleteMessage.getContentResolver().delete(  
//                    Uri.parse("content://sms/conversations/" + threadid[0]),  
//                    "_id = " + idelete, null);  
//            Log.e("delete_number", Integer.toString(number));  
//            if (number != 0)  
//                return 1;  
//            else  
//                return 0;  
//        } else {  
//            Log.e("number", "cann't fine the message!");  
//            return 0;  
//        }  
//    }  
//}  
