abstract sig Declaration {}
sig TranslationUnit {
	declares: set Declaration
}

abstract sig Identifier {}

abstract sig Variable {
	type: one Type//-Void
}

sig LocalVar, GlobalVar extends Variable {}

sig FunctionId, LocalVarDeclId, GlobalVarDeclId extends Identifier {}

sig Function  extends Declaration  {
	id: one FunctionId,
	returnType: one Type,
	param: one Variable,
	stmt: seq Stmt
}

pred wf[] {
/*
	all f:Function |
		f.returnType = Void => one f.stmt.elems&Return
		
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


all f:Function |
	(all e:f.stmt.elems.(If <: exp)   | e.ids = LocalVarDeclId  =>
		  e.ids in f.stmt.elems.(LocalVarDecl <: id)
		)

	

//all e:Expression |
//	e.ids = LocalVarDeclId => ( e.ids in Function.stmt.elems.(LocalVarDecl <: id) )


  // a variável de todo o returno terá que ser declarada dentro da funcao
/*	all f:Function |
		( 
			all re:f.stmt.elems | re in Return =>
			( all lvd:f.stmt.elems.(LocalVarDecl <:v)  |  re.(Return <: r)  = lvd)
		)
*/


all f:Function |
	(all re:f.stmt.elems | re in Return =>
			(   some lvd:f.stmt.elems | 
				lvd in LocalVarDecl and lvd.(LocalVarDecl <: v) = re.(Return <: r) 
			and f.stmt.idxOf[lvd] < f.stmt.idxOf[re] 	
		) 
		or
		re.(Return <: r) = f.param
	)






	// os parâmetros da função têm que ser diferetes das variáveis locais declaradas
	all f:Function | 
		(all lvd:f.stmt.elems | 	lvd in LocalVarDecl => 
			(all p:f.param | lvd.(LocalVarDecl <: v ) != p ) 
	)



	// as variaveis locais criadas na função, devem ser diferentes
	all f:Function | 
		(all lvd:f.stmt.elems | lvd in LocalVarDecl =>
			(some lvd2:f.stmt.elems | lvd.(LocalVarDecl <: v ) != lvd2.(LocalVarDecl <: v )
			)
		) 


	all if:If | 	one f:Function |  if in f.stmt.elems

	all e:Expression | one i:If | e in i.exp

	all id1:LocalVarDeclId | one local:LocalVarDecl | id1 in local.id	
	all id2:GlobalVarDeclId | one g:GlobalVarDecl | id2 in g.id
	all id3:FunctionId | one f:Function| id3 in f.id

	all f:Function  | f.id not in f.stmt.elems.exp.ids

	all gv2:GlobalVar | one gvd:GlobalVarDecl | gv2 in gvd.gv

	all lv2:LocalVar | one lvd:LocalVarDecl | lv2 in lvd.v

	all src:TranslationUnit | #src.declares > 0
	all d:Declaration | one src:TranslationUnit | d in src.declares
	
}

pred optimization[] {
	Stmt in Function.stmt.elems
	Type in Variable.type+Function.returnType 
	LocalVar in Function.param + LocalVarDecl.v 
	some Identifier
	all f:Function | #f.stmt < 4
	some f:Function | #f.stmt > 0
	all f:Function | not f.stmt.hasDups
	all e:Expression | (#e.ids = 2)
	#Function = 2
	#GlobalVarDecl = 1
	#LocalVarDecl = 1
	#If = 1
	#TranslationUnit = 1
//	all vr:Variable | one va:VarAttrib | vr = va.v => #va = 2
}

abstract sig Stmt {}
/*
sig VarAttrib extends Stmt { 
	v:  Variable
}
*/
sig Return extends Stmt { 
	r:Variable
}

sig LocalVarDecl extends Stmt { 
	id: one LocalVarDeclId,
	v: one LocalVar
}

sig GlobalVarDecl extends Declaration{ 
	id: one GlobalVarDeclId,
	gv: one GlobalVar
}

sig Expression {
	ids: set Identifier
}

sig If extends Stmt {
	exp: one Expression,
//	return: one Return
}

//sig Assignment extends Stmt {
//	va:Variable
//}

abstract sig Type {} 
//lone sig Void extends Type {} 
abstract sig BasicType extends Type {}
lone sig Integer_, Float,Char extends BasicType {}

fact { 
	wf[]
	optimization[]	
}

