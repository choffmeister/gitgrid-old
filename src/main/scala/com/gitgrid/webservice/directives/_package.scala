package com.gitgrid.webservice

import spray.routing.Directives

package object directives extends Directives
  with AuthDirectives
  with ODataDirectives
{
}
