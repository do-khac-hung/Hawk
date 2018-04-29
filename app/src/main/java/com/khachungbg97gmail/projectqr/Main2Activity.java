package com.khachungbg97gmail.projectqr;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;


public class Main2Activity extends TabActivity {
   static ArrayList<Item> model ;
    private TextView username = null;
    private TextView code = null;
    private TextView location = null;
    ItAdapter adapter1 = null;
   // Customer adapter1=null;
    private ToggleButton btLogFile;
    private Button scanBtnName, scanBtnId, scanBtnLocate,btSaveCsv,btSignout;
    private EditText contentTxtName, contentTxtId, contentTxtLocate;
    private ImageView rotate;
    private CheckBox checked;
    ConstraintLayout manHinh;
    int check = 0;
    Item current = null;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference data;
    FirebaseHelper helper;
    ListView list;

    FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        mAuth = FirebaseAuth.getInstance();
        model = new ArrayList<Item>();
        btLogFile=(ToggleButton)findViewById(R.id.logfile);
        scanBtnName = (Button) findViewById(R.id.name);
        scanBtnId = (Button) findViewById(R.id.pc);
        scanBtnLocate = (Button) findViewById(R.id.addr);
        btSaveCsv = (Button) findViewById(R.id.btcsv);
        contentTxtName = (EditText) findViewById(R.id.edtName);
        contentTxtId = (EditText) findViewById(R.id.edtid);
        contentTxtLocate = (EditText) findViewById(R.id.edtlocation);
        checked = (CheckBox) findViewById(R.id.checked);
        manHinh = (ConstraintLayout) findViewById(R.id.manhinh);
        rotate = (ImageView) findViewById(R.id.rotate);
        btSignout=(Button)findViewById(R.id.logout1);


        final Animation aim = AnimationUtils.loadAnimation(this, R.anim.anim_rotate);
        rotate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                view.startAnimation(aim);
            }
        });
        Button save = (Button) findViewById(R.id.save);

        save.setOnClickListener(onSave);

        list = (ListView) findViewById(R.id.history);


        intitFireBase();
        adapter1 = new ItAdapter();
        list.setAdapter(adapter1);
//        adapter1=new Customer(this,helper.retrieve());
//        list.setAdapter(adapter1);

        TabHost.TabSpec spec = getTabHost().newTabSpec("tag1");

        spec.setContent(R.id.history);
        spec.setIndicator("", getResources()
                .getDrawable(R.drawable.icons8));
        getTabHost().addTab(spec);

        spec = getTabHost().newTabSpec("tag2");
        spec.setContent(R.id.scanMN);
        spec.setIndicator("", getResources()
                .getDrawable(R.drawable.icons83));
        getTabHost().addTab(spec);

        spec = getTabHost().newTabSpec("tag3");
        spec.setContent(R.id.settings);
        spec.setIndicator("", getResources()
                .getDrawable(R.drawable.icons81));
        getTabHost().addTab(spec);
        getTabHost().setCurrentTab(1);


        scanBtnName.setOnClickListener(onScanName);
        scanBtnId.setOnClickListener(onScanId);
        scanBtnLocate.setOnClickListener(onScanLocate);
        checked.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checked.isChecked()) {
                    manHinh.setBackgroundResource(R.drawable.icons);
                    checked.setChecked(false);
                } else {
                    manHinh.setBackgroundResource(R.drawable.run);
                    checked.setChecked(true);
                }
            }
        });

        btSaveCsv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               String filename="cvsFile";
                saveFileCsv(filename,model);
            }
        });
        btSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                finish();
               startActivity(new Intent(Main2Activity.this,MainActivity.class));

            }
        });
        btLogFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                log();
            }
        });
    }
    private void log(){
        org.apache.log4j.Logger log= Log4jHelper.getLogger( "YourActivity" );
        log.error("Error");
        log.info("Info");
        log.warn("Warn");
    }
   private void saveFileCsv(String filename,ArrayList<Item>model1){
       String fileName=filename+".txt";
       File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath(),fileName);
       try {
           FileOutputStream fileOutputStream=new FileOutputStream(file);
           for (Item i:model1
                ) {
           String data=i.getUsername()+","+i.getCode()+","+i.getLocation()+"\n";
           fileOutputStream.write(data.getBytes());}
           fileOutputStream.close();
       } catch (FileNotFoundException e) {
           e.printStackTrace();
           Toast.makeText(this,"file not found",Toast.LENGTH_SHORT).show();
       } catch (IOException e) {
           e.printStackTrace();
           Toast.makeText(this,"Error Saving",Toast.LENGTH_SHORT).show();
       }

   }


    private void intitFireBase() {
        FirebaseApp.initializeApp(Main2Activity.this);
        firebaseDatabase=FirebaseDatabase.getInstance();
        data=firebaseDatabase.getReference();
        helper=new FirebaseHelper(data);
        DatabaseReference ref=data.child("Item");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot chidSnap : dataSnapshot.getChildren()) {
                    Item spacecraft=chidSnap.getValue(Item.class);
                    model.add(spacecraft);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }

    private View.OnClickListener onScanName = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.name) {
                IntentIntegrator integrator = new IntentIntegrator(Main2Activity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                check = 1;
                integrator.initiateScan();

            }
        }
    };
    private View.OnClickListener onScanId = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.pc) {
                IntentIntegrator integrator = new IntentIntegrator(Main2Activity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                check = 2;
                integrator.initiateScan();

            }
        }
    };
    private View.OnClickListener onScanLocate = new View.OnClickListener() {
        public void onClick(View v) {
            if (v.getId() == R.id.addr) {
                IntentIntegrator integrator = new IntentIntegrator(Main2Activity.this);
                integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
                integrator.setPrompt("Scan");
                integrator.setCameraId(0);
                integrator.setBeepEnabled(false);
                integrator.setBarcodeImageEnabled(false);
                check = 3;
                integrator.initiateScan();

            }
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "khong co noi dung", Toast.LENGTH_SHORT).show();
            } else {
                String scanContent = result.getContents();
                String[] words=scanContent.split(",");
                if (check == 1) {
                   // contentTxtName.setText(scanContent);
                    contentTxtName.setText(words[0]);
                    contentTxtId.setText(words[1]);
                    contentTxtLocate.setText(words[2]);
                } else if (check == 2) {
                    contentTxtId.setText(scanContent);
                } else if (check == 3) {
                    contentTxtLocate.setText(scanContent);
                }
            }
        } else super.onActivityResult(requestCode, resultCode, data);

    }
    class Customer extends BaseAdapter {
        Context context;
        ArrayList<Item> listItem;
        LayoutInflater inflater;
        public Customer(Context c,ArrayList<Item>list){
            this.context=c;
            this.listItem=list;
        }
       // Customer() {
//            super(Main2Activity.this, R.layout.row, model);
//        }

        @Override
        public int getCount() {
            return listItem.size();
        }

        @Override
        public Object getItem(int i) {
            return listItem.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if(inflater==null){
                inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }
            if(view==null){
                //view=inflater.inflate(R.layout.row,viewGroup,false);
               view= LayoutInflater.from(Main2Activity.this).inflate(R.layout.row,viewGroup,false);
            }
            //final Spacecraft s= (Spacecraft) this.getItem(position);
           // holder.populateFrom(model.get(i));
            TextView  username = (TextView)findViewById(R.id.user);
            TextView  code = (TextView) findViewById(R.id.code);
            TextView location = (TextView)findViewById(R.id.address);
            final Item s= (Item) this.getItem(i);

            username.setText(s.getUsername());
            code.setText(s.getCode());
            location.setText(s.getLocation());


            return view;
        }
    }



    class ItAdapter extends ArrayAdapter<Item> {
        ItAdapter() {
            super(Main2Activity.this, R.layout.row, model);
        }

        public View getView(int position, View convertView,
                            ViewGroup parent) {
            View row = convertView;
            ViewHolder holder = null;

            if (row == null) {
                LayoutInflater inflater = getLayoutInflater();

                row = inflater.inflate(R.layout.row, parent, false);
                holder = new ViewHolder(row);
                row.setTag(holder);
            } else {
                holder = (ViewHolder) row.getTag();
            }

            holder.populateFrom(model.get(position));

            return (row);
        }
    }

    static class ViewHolder {
        private TextView username = null;
        private TextView code = null;
        private TextView location = null;

        ViewHolder(View row) {
            username = (TextView) row.findViewById(R.id.user);
            code = (TextView) row.findViewById(R.id.code);
            location = (TextView) row.findViewById(R.id.address);
        }

        void populateFrom(Item r) {
            username.setText(r.getUsername());
            code.setText(r.getCode());
            location.setText(r.getLocation());
        }
    }
    public class FirebaseHelper {

        DatabaseReference db;
        Boolean saved=null;
        ArrayList<Item> spacecrafts=new ArrayList<>();

        public FirebaseHelper(DatabaseReference db) {
            this.db = db;
        }

        //WRITE IF NOT NULL
        public Boolean save(Item spacecraft)
        {
            if(spacecraft==null)
            {
                saved=false;
            }else
            {
                try
                {
                    db.child("Item").child(spacecraft.getCode()).setValue(spacecraft);
                   // db.child("Item1").push().setValue(spacecraft);
                    saved=true;

                }catch (DatabaseException e)
                {
                    e.printStackTrace();
                    saved=false;
                }
            }

            return saved;
        }
        //IMPLEMENT FETCH DATA AND FILL ARRAYLIST
        private void fetchData(DataSnapshot dataSnapshot)
        {
            spacecrafts.clear();

            for (DataSnapshot ds : dataSnapshot.getChildren())
            {
                Item spacecraft=ds.getValue(Item.class);
                spacecrafts.add(spacecraft);
                model.add(spacecraft);
            }

            adapter1.notifyDataSetChanged();

        }
        //READ BY HOOKING ONTO DATABASE OPERATION CALLBACKS
        public ArrayList<Item> retrieve() {
            db.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    fetchData(dataSnapshot);
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    fetchData(dataSnapshot);

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {

                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            return spacecrafts;
        }
    }

    private View.OnClickListener onSave = new View.OnClickListener() {
        public void onClick(View v) {
            current = new Item();
            current.setUsername(contentTxtName.getText().toString());
            current.setLocation(contentTxtLocate.getText().toString());
            current.setCode(contentTxtId.getText().toString());
            data.child("Item").child(current.getCode()).setValue(current);
            model.add(current);
            //helper.save(current);


            adapter1.notifyDataSetChanged();
        }
    };


}

