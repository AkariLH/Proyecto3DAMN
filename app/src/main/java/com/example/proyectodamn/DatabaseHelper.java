package com.example.proyectodamn;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre y versión de la base de datos
    private static final String DATABASE_NAME = "ProgramA6.db";
    private static final int DATABASE_VERSION = 3;

    // Tabla de configuraciones
    public static final String TABLE_CONFIGURACIONES = "configuraciones";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_USUARIO = "usuario";
    public static final String COLUMN_VOLUMEN = "volumen";
    public static final String COLUMN_PROGRAMA = "programa";
    public static final String COLUMN_RUIDO_BLANCO = "ruido_blanco";

    // Tabla de usuarios
    public static final String TABLE_USUARIOS = "usuarios";
    public static final String COLUMN_CONTRASEÑA = "contraseña";

    // SQL para crear las tablas
    private static final String TABLE_CREATE_CONFIGURACIONES =
            "CREATE TABLE " + TABLE_CONFIGURACIONES + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_USUARIO + " TEXT NOT NULL, " +
                    COLUMN_VOLUMEN + " INTEGER, " +
                    COLUMN_PROGRAMA + " TEXT, " +
                    COLUMN_RUIDO_BLANCO + " INTEGER" + ");";

    private static final String TABLE_CREATE_USUARIOS =
            "CREATE TABLE " + TABLE_USUARIOS + " (" +
                    COLUMN_USUARIO + " TEXT PRIMARY KEY, " +
                    COLUMN_CONTRASEÑA + " TEXT NOT NULL);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear tablas
        db.execSQL(TABLE_CREATE_CONFIGURACIONES);
        db.execSQL(TABLE_CREATE_USUARIOS);

        // Insertar un usuario inicial (admin)
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO, "admin");
        values.put(COLUMN_CONTRASEÑA, "1234");
        db.insert(TABLE_USUARIOS, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Elimina las tablas si existen
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONFIGURACIONES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USUARIOS);
        onCreate(db); // Recrea las tablas
    }

    // Método para insertar un nuevo usuario
    public void insertarUsuario(String usuario, String contraseña) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO, usuario);
        values.put(COLUMN_CONTRASEÑA, contraseña);
        db.insert(TABLE_USUARIOS, null, values);
        db.close();
    }

    // Método para validar un usuario y su contraseña
    public boolean validarUsuario(String usuario, String contraseña) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_USUARIOS, // Nombre de la tabla
                null, // Todas las columnas
                COLUMN_USUARIO + "=? AND " + COLUMN_CONTRASEÑA + "=?", // Condición WHERE
                new String[]{usuario, contraseña}, // Argumentos WHERE
                null,
                null,
                null
        );
        boolean existe = cursor.moveToFirst(); // Devuelve true si hay registros
        cursor.close();
        return existe;
    }

    // Métodos para configuraciones
    public void insertarConfiguracion(String usuario, int volumen, String programa, boolean ruidoBlanco) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USUARIO, usuario);
        values.put(COLUMN_VOLUMEN, volumen);
        values.put(COLUMN_PROGRAMA, programa);
        values.put(COLUMN_RUIDO_BLANCO, ruidoBlanco ? 1 : 0);
        db.insert(TABLE_CONFIGURACIONES, null, values);
        db.close();
    }

    public void actualizarConfiguracion(String usuario, int volumen, String programa, boolean ruidoBlanco) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_VOLUMEN, volumen);
        values.put(COLUMN_PROGRAMA, programa);
        values.put(COLUMN_RUIDO_BLANCO, ruidoBlanco ? 1 : 0);
        db.update(TABLE_CONFIGURACIONES, values, COLUMN_USUARIO + "=?", new String[]{usuario});
        db.close();
    }

    public Cursor obtenerConfiguracion(String usuario) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.query(
                TABLE_CONFIGURACIONES,
                null,
                COLUMN_USUARIO + "=?",
                new String[]{usuario},
                null,
                null,
                null
        );
    }
}
