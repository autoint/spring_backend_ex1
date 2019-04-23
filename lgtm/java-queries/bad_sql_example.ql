/**
 * @name Query built without neutralizing special characters
 * @description Building a SQL or Java Persistence query without escaping or otherwise neutralizing any special
 *              characters is vulnerable to insertion of malicious code.
 * @kind path-problem
 * @problem.severity error
 * @precision high
 * @id java/concatenated-sql-query
 * @tags security
 *       external/cwe/cwe-089
 */

import semmle.code.java.security.SqlUnescapedLib
import semmle.code.java.frameworks.Jdbc
import DataFlow::PathGraph

class UncontrolledStringBuilderSource extends DataFlow::ExprNode {
  UncontrolledStringBuilderSource() {
    exists(StringBuilderVar sbv |
      uncontrolledStringBuilderQuery(sbv, _) and
      this.getExpr() = sbv.getToStringCall()
    )
  }
}

class UncontrolledStringFormatSource extends DataFlow::ExprNode {
  UncontrolledStringFormatSource() {
    exists(MethodAccess ma, Expr e |
    ma.getMethod().getName() = "format" and
    ma.getAnArgument() = e and
    not controlledString(e) and
    this.asExpr() = ma)
  }
}

class UncontrolledStringBuilderSourceFlowConfig extends TaintTracking::Configuration {
  UncontrolledStringBuilderSourceFlowConfig() {
    this = "SqlUnescaped::UncontrolledStringBuilderSourceFlowConfig"
  }

  override predicate isSource(DataFlow::Node src) { 
  	src instanceof UncontrolledStringBuilderSource or 
  	src instanceof UncontrolledStringFormatSource 
  }

  override predicate isSink(DataFlow::Node sink) { sink.asExpr() instanceof SqlExpr }

  override predicate isSanitizer(DataFlow::Node node) {
    node.getType() instanceof PrimitiveType or node.getType() instanceof BoxedType
  }
}

/* sources
from UncontrolledStringFormatSource d
select d
*/

/* sinks 
from SqlExpr s
select s
*/

from UncontrolledStringBuilderSourceFlowConfig cnfg, DataFlow::PathNode source, DataFlow::PathNode sink
where cnfg.hasFlowPath(source, sink)
select source, source, sink, "bad sql"
