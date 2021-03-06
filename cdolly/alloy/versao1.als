abstract sig Declaration {}
sig TranslationUnit {
	declares: set Declaration
}

abstract sig Identifier {}

abstract sig Variable {
	type: one Type-Void
}

sig  GlobalVar extends Variable {}

//sig LocalVar, GlobalVar extends Variable {}

sig  GlobalVarDeclId extends Identifier {}
//sig FunctionId, LocalVarDeclId, GlobalVarDeclId extends Identifier {}
/*
sig Function  extends Declaration  {
	id: one FunctionId,
	returnType: one Type,
	param: one Variable,
//	stmt: seq Stmt
}*/

pred wf[] {
//	all f:Function |
//f.returnType = Void => one f.stmt.elems&Return
	/*	
	all f:Function | 
		f.returnType != Void => f.stmt.last in Return
	
	all f:Function | 
		f.returnType != Void => one f.stmt.elems&Return

	all f:Function | 
		f.returnType != Void => f.stmt.last.r.type = f.returnType	
	*/	
/*
	all f:Function |
		(all st1:f.stmt.elems | st1 in VarAttrib => 
			(some st2:f.stmt.elems | 
				st2 in LocalVarDecl and st2.(LocalVarDecl <: v ) = st1.(VarAttrib <: v )
			)
			 or
					st1.(VarAttrib <: v ) = f.param.v
		)
*/
//ou 
// se variabel for um dos elementos de st1, entao st1.(VarAttrib <: v ) tem que ser igual ao parametro



	//all f:Function | all g:GlobalVarDecl |// f.param != g
		//g.(GlobalVar <: gv) != f.param


	// os parâmetros da função têm que ser diferetes das variáveis locais declaradas
/*	all f:Function | 
		(all lvd:f.stmt.elems | 
			lvd in LocalVarDecl => (some p:f.param | lvd.(LocalVarDecl <: v ) != p ) )

	// as variaveis locais criadas na função, devem ser diferentes
	all f:Function | 
		(all lvd:f.stmt.elems | lvd in LocalVarDecl =>
			(some lvd2:f.stmt.elems | lvd.(LocalVarDecl <: v ) != lvd2.(LocalVarDecl <: v )
			)
		) 

	all e:Expression | one i:If | e in i.exp



	all f:Function  | f.id not in f.stmt.elems.exp.ids



	*/
//	all id1:LocalVarDeclId | one local:LocalVarDecl | id1 in local.id	
	all id2:GlobalVarDeclId | one g:GlobalVarDecl | id2 in g.id
//	all id3:FunctionId | one f:Function| id3 in f.id
	all gv2:GlobalVar | one gvd:GlobalVarDecl | gv2 in gvd.gv

	//all lv2:LocalVar | one lvd:LocalVarDecl | lv2 in lvd.v
	all src:TranslationUnit | #src.declares > 0
	all d:Declaration | one src:TranslationUnit | d in src.declares
}

pred optimization[] {
//	Stmt in Function.stmt.elems
//	Type in Variable.type+Function.returnType 
//	LocalVar in Function.param + LocalVarDecl.v 
	some Identifier
	//all f:Function | #f.stmt < 4
//	some f:Function | #f.stmt > 0
//	all f:Function | not f.stmt.hasDups
	//all e:Expression | (#e.ids = 2)
//	#Function = 2
//	#GlobalVarDecl
	#TranslationUnit =1
	//#If = 1
	//all vr:Variable | one va:VarAttrib | vr = va.v => #va = 2
}
/*
sig LocalVarDecl { 
	id: one LocalVarDeclId,
	v: one LocalVar
}
*/
sig GlobalVarDecl   extends Declaration{ 
	id: one GlobalVarDeclId,
	gv: one GlobalVar
}


abstract sig Type {} 
lone sig Void extends Type {} 
abstract sig BasicType extends Type {}
lone sig Integer_, Float,Char extends BasicType {}

fact { 
	wf[]
	optimization[]	
}

