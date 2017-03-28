package com.globalsoft.SapLibSoap.Constraints;

import java.io.Serializable;
import java.util.Vector;

import org.ksoap2.serialization.KvmSerializable;

public class ContactProVectSerializable implements Serializable  {
	
	private Vector vectVal;
	
	public ContactProVectSerializable(){		
	}
	
	public ContactProVectSerializable(Vector vect_arg){
		this.vectVal = vect_arg;
	}
	
	public void setVector(Vector vect_arg){
		this.vectVal = vect_arg;
	}//fn setVector
	
	public Vector getVector(){
		return this.vectVal;
	}//fn getVector
	
}//End of class ContactProVectSerializable
