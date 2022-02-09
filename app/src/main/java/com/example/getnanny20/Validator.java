package com.example.getnanny20;

import android.text.Editable;
import android.text.TextWatcher;

import androidx.annotation.NonNull;

import com.example.getnanny20.User;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Validator {

    public abstract static class Watcher {

        private String error;
        private Watcher(String error) {
            this.error = error;
        }

        public abstract boolean privateCheck(String input);
    }

    public static class Watcher_Email extends Watcher {
        private boolean isExist = false;
        public Watcher_Email(String error) {
            super(error);
        }
        @Override
        public boolean privateCheck(String input) {
            String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
            Pattern pat = Pattern.compile(ePattern);
            if (!pat.matcher(input).matches()) {
                return false;
            }

            MyFirebaseDB.CallBack_Users callBack_users = new MyFirebaseDB.CallBack_Users() {
                @Override
                public void dataReady(ArrayList<User> users) {
                    for (User user : users) {
                        if (user.getEmail().equals(input))
                            isExist = true;
                    }
                }
            };

            if (isExist == true)
                return false;

            return true;
        }
    }

    public static class Watcher_Password extends Watcher {

        public Watcher_Password(String error) {
            super(error);
        }
        @Override
        public boolean privateCheck(String input) {
            String ePattern = "^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20}$";
            Pattern pat = Pattern.compile(ePattern);
            if (!pat.matcher(input).matches()) {
                return false;
            }
            return true;
        }
    }

    public static class Watcher_Phone extends Watcher {

        public Watcher_Phone(String error) {
            super(error);
        }
        @Override
        public boolean privateCheck(String input) {
            if (input.matches("[0-9]+") && input.length() == 10)
                return true;
            return false;
        }
    }

    public static class Watcher_Username extends Watcher {

        public Watcher_Username(String error) {
            super(error);
        }
        @Override
        public boolean privateCheck(String input) {
            String ePattern = "^(?=.{5,12}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";
            Pattern pat = Pattern.compile(ePattern);
            if (!pat.matcher(input).matches()) {
                return false;
            }
            return true;
        }
    }


    public static class Builder {
        private TextInputLayout textInputLayout;
        private ArrayList<Watcher> watchers = new ArrayList<>();
        private boolean isAlreadyBuild = false;

        public static Builder make(@NonNull TextInputLayout textInputLayout) {
            return new Builder(textInputLayout);
        }

        private Builder(@NonNull TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        public Builder addWatcher(Watcher watcher) {
            if (!isAlreadyBuild) {
                this.watchers.add(watcher);
            }
            return this;
        }

        public Builder build() {
            if (!isAlreadyBuild) {
                isAlreadyBuild = true;
                addValidator(textInputLayout, watchers);
            }
            return this;
        }
    }

    private static void addValidator(TextInputLayout textInputLayout, ArrayList<Watcher> watchers) {
        textInputLayout.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String input = charSequence.toString();

                boolean result = true;
                for (Watcher watcher : watchers) {
                    result = watcher.privateCheck(input);
                    if (!result) {
                        textInputLayout.setError(watcher.error);
                        break;
                    }
                }

                if (result) {
                    textInputLayout.setError("");
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }



}
