package cdolly.model.inspector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import cdolly.model.Entity;
import cdolly.model.Relation;
import edu.mit.csail.sdg.alloy4.SafeList;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig;
import edu.mit.csail.sdg.alloy4compiler.ast.Sig.Field;
import edu.mit.csail.sdg.alloy4compiler.translator.A4Solution;

/**
 * @author Jeanderson Candido<br>
 *         <a target="_blank"
 *         href="http://jeandersonbc.github.io">http://jeandersonbc
 *         .github.io</a>
 * 
 */
public class AlloyInspector implements IModelVisitor {

	private static final String CORE_ID = "core/";
	private A4Solution model;

	/**
	 * @param toBeInspected
	 *            The Alloy instance ({@link A4Solution}) to be inspected.
	 */
	public AlloyInspector(A4Solution toBeInspected) {
		if (toBeInspected != null) {
			this.model = toBeInspected;
		}
	}
	// getTranslationUnits retorna todas as assinaturas existens no código em Alloy
	@Override
	public List<String> getTranslationUnits() {
		Sig sig = getSigByEntity(Entity.TRANSLATION_UNIT);
		List<String> names = new ArrayList<String>();
		if (sig != null) {
			String rawNames = this.model.eval(sig).toString();
			for (String name : rawNames.split(",")) {
				names.add(removeBraces(name));
			}
		}
		return names;
	}
	
	@Override
	public List<String> getNameVariable(String functionDeclaration) {
		List<String> declarations = new LinkedList<String>();
		Sig sig = getSigByEntity(Entity.FUNCTION);
		SafeList<Field> fields = sig.getFields();
		for (Field f : fields) {
			
			if (f.toString().contains(Entity.FUNCTION.label()) && f.toString().contains(Relation.PARAM.label())) {
			
				String list_param = removeBraces(this.model.eval(f).toString());
				if (list_param.contains("Global") ) {
					for (String param : list_param.split(",")) {
						Sig sig2 = getSigByEntity(Entity.GLOBAL_VAR_DECL);
						SafeList<Field> fields2 = sig2.getFields();
						for (Field f2 : fields2) {
							String list_gloval_var = removeBraces(this.model.eval(f2).toString());
							for (String g_v : list_gloval_var.split(",")) {
								if (g_v.contains(param.split("->")[1]) ) {
									declarations.add(g_v.split("->")[0]);
									return declarations;
									
								}
							}
							
						}
					}
				}
				else if (list_param.contains("Local")) {
					for (String param : list_param.split(",")) {
						Sig sig2 = getSigByEntity(Entity.LOCAL_VAR_DECL);
						SafeList<Field> fields2 = sig2.getFields();
						for (Field f2 : fields2) {
							String list_local_var = removeBraces(this.model.eval(f2).toString());
							for (String v : list_local_var.split(",")) {
								if (v.contains(param.split("->")[1])) {
									declarations.add(v.split("->")[0]);
									return declarations;
									
								}
							}
							
						}
					}
				}
			}
		}
		return declarations;
	}
	

	@Override
	public String getTeste() {
		
		Sig sig = getSigByEntity(Entity.FUNCTION);
		SafeList<Field> fields = sig.getFields();
		
		for (Field f : fields) {
			if (f.toString().contains(Entity.FUNCTION.label()) && f.toString().contains(Relation.PARAM.label())) {
								
				String teste = removeBraces(this.model.eval(f).toString());
				
				
				
				for (String t : teste.split(",")) {
					
						Sig sig2 = getSigByEntity(Entity.GLOBAL_VAR_DECL);
						SafeList<Field> fields2 = sig2.getFields();
						for (Field f2 : fields2) {
							if (f2.toString().contains(Entity.GLOBAL_VAR_DECL.label()) && f2.toString().contains(Relation.G_V.label())) {
								String list_gloval_var = removeBraces(this.model.eval(f2).toString());
								for (String g_v : list_gloval_var.split(",")) {
									if (g_v.contains(t.split("->")[1])) {
									}
								}
								
							}
							
						}
					
				}
			}
		}
		
		return "";
	}
	
	@Override
	public List<String> getGlobalDeclarationsFrom(String translationUnit) {
		return getDeclarationsFrom(translationUnit, Entity.TRANSLATION_UNIT,
				Relation.DECLARES);
	}

	@Override
	public String getFunctionNameFrom(String declaration) {
		return getNameBy(Entity.FUNCTION, declaration);
	}

	@Override
	public List<String> getParametersFrom(String functionDeclaration) {
		return getDeclarationsFrom(functionDeclaration, Entity.FUNCTION,
				Relation.PARAM);
	}

	@Override
	public String getReturnTypeFrom(String declaration) {
		return getTypeFrom(declaration, Entity.FUNCTION, Relation.RETURN_TYPE);
	}
	
	@Override
	public List<String> getLocalDeclarationsFrom(String functionDeclaration) {
		List<String> declarations = new LinkedList<String>();
		Sig sig = getSigByEntity(Entity.FUNCTION);
		SafeList<Field> fields = sig.getFields();
		for (Field f : fields) {
			if (f.toString().contains(Entity.FUNCTION.label()) && f.toString().contains(Relation.STMT.label())) {
				
				String list_local_variable = removeBraces(this.model.eval(f).toString());
				for (String variable : list_local_variable.split(",")) {
					if (variable.contains("LocalVarDecl") && variable.contains(functionDeclaration)) {
						declarations.add(variable.split("->")[2]);
					}
				}
			}
		}
		
		
		return declarations;
	}

	@Override
	public String getDeclaringTypeFrom(String variableDeclaration,boolean isGlobal) {
		return getTypeFrom(variableDeclaration,
				(isGlobal ? Entity.VARIABLE : Entity.VARIABLE),
				Relation.TYPE);
	}
	
	@Override
	public String getGlobalVarFrom(String GlobalVarDecl) {
		String global_var = null;
		Sig sig = getSigByEntity(Entity.GLOBAL_VAR_DECL);
		SafeList<Field> fields = sig.getFields();
		for (Field f : fields) {
			if (f.toString().contains(Entity.GLOBAL_VAR_DECL.label()) && f.toString().contains(Relation.G_V.label())) {
				for (String global_var_decl : removeBraces(this.model.eval(f).toString()).split(",")) {
					if (global_var_decl.contains(GlobalVarDecl)) {
						global_var = global_var_decl.split("->")[1];
					}
				}
			}
		}
		
		return global_var;
	}
	@Override
	public String getLocalVarFrom(String LocalVarDecl) {
		String local_var = null;
		Sig sig = getSigByEntity(Entity.LOCAL_VAR_DECL);
		SafeList<Field> fields = sig.getFields();
		for (Field f : fields) {
			if (f.toString().contains(Entity.LOCAL_VAR_DECL.label()) && f.toString().contains(Relation.V.label())) {
				String local_var_decl_list   = removeBraces(this.model.eval(f).toString());
				for (String local_var_decl : local_var_decl_list.split(",")) {
					if (local_var_decl.contains(LocalVarDecl)) {
						
						local_var = local_var_decl.split("->")[1];
					}
				}
			}
		}
		return local_var;
	}
	
	@Override
	public String getDeclaringTypeFrom2(String variableDeclaration,
			boolean isGlobal) {

		return getTypeFrom(variableDeclaration,
				(isGlobal ? Entity.GLOBAL_VAR : Entity.LOCAL_VAR),
				Relation.TYPE);
	}


	@Override
	public IStringChecker getStringChecker() {
		return StringCheckerImpl.getInstance();
	}

	@Override
	public String getVariableNameFrom(String declaration, boolean isGlobal) {
		return getNameBy((isGlobal ? Entity.GLOBAL_VAR_DECL	: Entity.LOCAL_VAR_DECL), declaration);
	}
	
	@Override
	public String getypeVariableFrom(String declaration) {
		Sig sig = null;
		String return_var = null;
		if (declaration.contains("Global")) {
			sig = getSigByEntity(Entity.GLOBAL_VAR_DECL);
			SafeList<Field> fields = sig.getFields();
			for (Field f : fields) {
				String list_variables = removeBraces(this.model.eval(f).toString());
				if (f.toString().contains(Entity.GLOBAL_VAR_DECL.label()) && f.toString().contains(Relation.ID.label())) {
					for (String global_var_temp : list_variables.split(",")) {
						if (global_var_temp.contains(declaration.replace("_", "$"))) {
							String global_var_decl = global_var_temp.split("->")[0];
							for (Field f2 : fields) {
								list_variables = removeBraces(this.model.eval(f2).toString());
								if (f2.toString().contains(Entity.GLOBAL_VAR_DECL.label()) && f2.toString().contains(Relation.G_V.label())) {
									for (String global_var_temp2 : list_variables.split(",")) {
										if (global_var_temp2.contains(global_var_decl)) {
											return_var = global_var_temp2.split("->")[1];
											sig = getSigByEntity(Entity.VARIABLE);
											fields = sig.getFields();
											for (Field f3 : fields) {
												list_variables = removeBraces(this.model.eval(f3).toString());
												if (f3.toString().contains(Entity.VARIABLE.label()) && f3.toString().contains(Relation.TYPE.label())) {
													for (String variable_temp : list_variables.split(",")) {
														if (variable_temp.contains(return_var)) {
															return_var = variable_temp.split("->")[1];
														}
													}
												}
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		else if (declaration.contains("Local")) {
			sig = getSigByEntity(Entity.LOCAL_VAR_DECL);
			SafeList<Field> fields = sig.getFields();
			for (Field f : fields) {
				String list_variables = removeBraces(this.model.eval(f).toString());
				if (f.toString().contains(Entity.LOCAL_VAR_DECL.label()) && f.toString().contains(Relation.ID.label())) {
					for (String global_var_temp : list_variables.split(",")) {
						if (global_var_temp.contains(declaration.replace("_", "$"))) {
							String global_var_decl = global_var_temp.split("->")[0];
							for (Field f2 : fields) {
								String list_variables2 = removeBraces(this.model.eval(f2).toString());
								if (f2.toString().contains(Entity.LOCAL_VAR_DECL.label()) && f2.toString().contains(Relation.V.label())) {
									for (String global_var_temp2 : list_variables2.split(",")) {
										if (global_var_temp2.contains(global_var_decl)) {
											return_var = global_var_temp2.split("->")[1];
											sig = getSigByEntity(Entity.VARIABLE);
											fields = sig.getFields();
											for (Field f3 : fields) {
												list_variables = removeBraces(this.model.eval(f3).toString());
												if (f3.toString().contains(Entity.VARIABLE.label()) && f3.toString().contains(Relation.TYPE.label())) {
													for (String variable_temp : list_variables.split(",")) {
														if (variable_temp.contains(return_var)) {
															return_var = variable_temp.split("->")[1];
														}
													}
												}
												
											}
										}
									}
								}
							}
						}
					}
				}
			}
		
		}
		
		
		
		return return_var;
	}

	@Override
	public String getReturnStmtFrom(String declaration) {
		String returnStm = "" ;
		Sig sig = getSigByEntity(Entity.FUNCTION);
		SafeList<Field> fields = sig.getFields();
		for (Field f : fields) {
			if (f.toString().contains(Entity.FUNCTION.label()) && f.toString().contains(Relation.STMT.label())) {
				
				String list_return = removeBraces(this.model.eval(f).toString());
				for (String returnStmt : list_return.split(",")) {
					if (returnStmt.contains(declaration) && returnStmt.contains("->core/Return") ) {
						String r = returnStmt.split("->")[2]; // nome do retorno da funcao correta
						Sig sig2 = getSigByEntity(Entity.RETURN);
						SafeList<Field> fields2 = sig2.getFields();
						
						for (Field f2 : fields2) {
							String list_return2 = removeBraces(this.model.eval(f2).toString());
						
							for (String returnStmt2 : list_return2.split(",")) {
								if (returnStmt2.contains(r)) {
									String variable_return = returnStmt2.split("->")[1];
									// buscar o nome da variavel global através do globalVar
									Sig sig3 = null;
									if (variable_return.split("->")[0].contains("Global")) {
										sig3 = getSigByEntity(Entity.GLOBAL_VAR_DECL);
									}
									else if (variable_return.split("->")[0].contains("Local")) {
										sig3 = getSigByEntity(Entity.LOCAL_VAR_DECL);
									}
									
									SafeList<Field> fields3 = sig3.getFields();
									
									for (Field f3 : fields3) {
										String list_variables = removeBraces(this.model.eval(f3).toString());
										if (f3.toString().contains(Entity.GLOBAL_VAR_DECL.label()) && f3.toString().contains(Relation.G_V.label())) {
											for (String global_var : list_variables.split(",")) {
												if (global_var.contains(variable_return)) {
													String global_var_decl = global_var.split("->")[0];
													
													Sig sig4 = getSigByEntity(Entity.GLOBAL_VAR_DECL);
													SafeList<Field> fields4 = sig4.getFields();
													
													for (Field f4 : fields4) {
														String list_global_var2 = removeBraces(this.model.eval(f4).toString());
														if (f4.toString().contains(Entity.GLOBAL_VAR_DECL.label()) && f4.toString().contains(Relation.ID.label())) {
															for (String global_var2 : list_global_var2.split(",")) {
																if (global_var2.contains(global_var_decl)) {
																	returnStm = global_var2.split("->")[1];
																	
																}
																
															}
														}
													}
												}
											}
										}
										
										else if (f3.toString().contains(Entity.LOCAL_VAR_DECL.label()) && f3.toString().contains(Relation.V.label())) {
											for (String global_var : list_variables.split(",")) {
												if (global_var.contains(variable_return)) {
													String global_var_decl = global_var.split("->")[0];
													
													Sig sig4 = getSigByEntity(Entity.LOCAL_VAR_DECL);
													SafeList<Field> fields4 = sig4.getFields();
													
													for (Field f4 : fields4) {
														String list_global_var2 = removeBraces(this.model.eval(f4).toString());
														if (f4.toString().contains(Entity.LOCAL_VAR_DECL.label()) && f4.toString().contains(Relation.ID.label())) {
															for (String global_var2 : list_global_var2.split(",")) {
																if (global_var2.contains(global_var_decl)) {
																	returnStm = global_var2.split("->")[1];
																	
																}
																
															}
														}
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		
		return returnStm;
	}
	
	@Override
	public  List<String> getReturnIfStatementFrom(String declaration) {
		List<String> listExpressionStatement = new LinkedList<String>();
	
		Sig sig = getSigByEntity(Entity.FUNCTION);
		SafeList<Field> fields = sig.getFields();
		
		for (Field f : fields) {
			if (f.toString().contains(Entity.FUNCTION.label()) && f.toString().contains(Relation.STMT.label())) {
				String list_function = removeBraces(this.model.eval(f).toString());
				
				for (String function : list_function.split(",")) {
					if (function.toString().contains(Entity.FUNCTION.label()) && function.toString().contains(Relation.IF.label())) {
						if ( function.contains(declaration) && function.contains("->core/If$") ) {
							Sig sigIfStatement = getSigByEntity(Entity.IF);
							SafeList<Field> fieldsIfStatement = sigIfStatement.getFields();
							
							for (Field field : fieldsIfStatement) {
								if (field.toString().contains(Entity.IF.label()) && field.toString().contains(Relation.EXPRESSION.label())) {
									String listIfStatment = removeBraces(this.model.eval(field).toString());
									String expression = listIfStatment.split(">")[1];
									Sig sigExpression = getSigByEntity(Entity.EXPRESSION);
									SafeList<Field> fieldsExpression = sigExpression.getFields();
									for (Field fieldExpression : fieldsExpression) {
										if (fieldExpression.toString().contains(Entity.EXPRESSION.label()) && fieldExpression.toString().contains(Relation.IDS.label())) {
											String listExpression = removeBraces(this.model.eval(fieldExpression).toString());
											for (String arg : listExpression.split(",")) {
												
												if (arg.contains(expression)) {
													
													listExpressionStatement.add(arg.split("->")[1].replace("core/", "").replace("$", "_"));
													
												}
											}
											
										}
									
									}
									
								}
							}
						}
						
					}
					
					
				}
			}
		}
		return listExpressionStatement;
	}
	

	private List<String> getDeclarationsFrom(String targetDeclaration,Entity belongingEntity, Relation relationKind) {
		
		String rawDeclarations = getRawDeclaration(belongingEntity,	relationKind);
		List<String> declarations = new LinkedList<String>();
		for (String declaration : rawDeclarations.split(", ")) {
			if (declaration.contains(targetDeclaration)) {
				declarations.add(removePrefix(belongingEntity, declaration));
			}
		}
		return declarations;
	}

	private String getTypeFrom(String typedDeclaration, Entity entity,Relation relationType) {
		String rawDeclarations = getRawDeclaration(entity, relationType);
		String rawType = "";
		for (String name : rawDeclarations.split(", ")) {
			if (name.contains(typedDeclaration)) {
				rawType = removePrefix(entity, name);
				return rawType.replace(CORE_ID, "").replaceAll("\\$[0-9]*", "");
			}
		}
		return rawType;
	}
	// verifica as relações entre as assinaturas
	private Field getFieldBy(Entity entity, Relation relation) {
		Sig sig = getSigByEntity(entity);
		SafeList<Field> fields = sig.getFields();
		
		for (Field f : fields) {
			
			if (f.toString().contains(entity.label())
					&& f.toString().contains(relation.label())) {
				return f;
			}
				
		}
		throw new FieldNotFoundException(entity, relation);
	}

	private String getNameBy(Entity entity, String declaration) {
		String rawDeclarations = getRawDeclaration(entity, Relation.ID);
		String target = "";
		for (String name : rawDeclarations.split(", ")) {
			if (name.contains(declaration)) {
				target = removePrefix(entity, name);
				return target.replace(CORE_ID, "").replace("$", "_");
			}
		}
		
		return target;

	}

	private String getRawDeclaration(Entity entity, Relation relation) {
		Field field = getFieldBy(entity, relation);
		return removeBraces(this.model.eval(field).toString());
	}

	private Sig getSigByEntity(Entity kind) {
		if (kind != null) {
			SafeList<Sig> allSigsFromModel = this.model.getAllReachableSigs();
			String pureSigName = null;
			for (Sig sig : allSigsFromModel) {
				pureSigName = sig.toString().replace(CORE_ID, "");
				if (pureSigName.equals(kind.label())) {
					return sig;
				}
			}
		}
		throw new SigNotFoundException(kind.label());
	}
	
	private String removePrefix(Entity entity, String rawDeclarations) {
		
		StringBuilder regex = new StringBuilder(CORE_ID);
		regex.append(entity.label());
		regex.append("\\$[0-9]+\\->");
		return rawDeclarations.replaceAll(regex.toString(), "");
	}
	
	private String removeBraces(String name) {
		return name.replace("{", "").replace("}", "");
	}
	@Override
	public String getIfsFrom(String functionDeclaration) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<String> getVariablesFrom(String declaration) {
		// TODO Auto-generated method stub
		return null;
	}

}
