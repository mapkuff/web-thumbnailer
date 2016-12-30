//package com.prime.web.thumbnailer.test.config;
//
//import java.util.Locale;
//
//import org.hibernate.boot.model.naming.Identifier;
//import org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl;
//import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
//
//public class HibernatePhysicalNamingStrategyImpl extends PhysicalNamingStrategyStandardImpl {
//	
//    private static final long serialVersionUID = -3020615242092992933L;
//
//    @Override
//	public Identifier toPhysicalSequenceName(Identifier name, JdbcEnvironment context) {
//		return super.toPhysicalSequenceName(name, context);
//	}
//    
//	@Override
//	public Identifier toPhysicalTableName(Identifier name, JdbcEnvironment context) {
//		return new Identifier(addUnderscores(name.getText()).toUpperCase(), name.isQuoted());
//	}
//
//
//	@Override
//	public Identifier toPhysicalColumnName(Identifier name, JdbcEnvironment context) {
//		return new Identifier(addUnderscores(name.getText()).toUpperCase(), name.isQuoted());
//	}
//	
//	protected String addUnderscores(String name) {
//	     final StringBuilder buf = new StringBuilder( name.replace('.', '_') );
//	     for (int i=1; i<buf.length()-1; i++) {
//	        if (
//	             Character.isLowerCase( buf.charAt(i-1) ) &&
//	             Character.isUpperCase( buf.charAt(i) ) &&
//	             Character.isLowerCase( buf.charAt(i+1) )
//	         ) {
//	             buf.insert(i++, '_');
//	         }
//	     }
//	     return buf.toString().toLowerCase(Locale.ROOT);
//	 }
//    
//}