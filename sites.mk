# ======================================================================
# Makefile - targets for site-specific packages of the VOLANO software
# Copyright (C) 2014-2020 John Neffenger
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU General Public License as published by
# the Free Software Foundation, either version 3 of the License, or
# (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU General Public License for more details.
#
# You should have received a copy of the GNU General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
# ======================================================================

# Local shell scripts
siteprep = rm -rf ria/*

# Free trial download
.PHONY: localhost
localhost:
	$(siteprep)
	$(MAKE) keys upgrade product checksum

# Sample site-specific targets
.PHONY: example-com
example-com: export SITENAME = $@
example-com: export CODEBASE = *.example.com
example-com: export DOCBASE  = *.example.com *.example.net *.example.org
example-com:
	$(siteprep)
	$(MAKE) keys upgrade product checksum

# Testing package
.PHONY: status6-ca
status6-ca: export SITENAME = $@
status6-ca: export CODEBASE = *.status6.ca
status6-ca: export DOCBASE  = *.status6.ca
status6-ca:
	$(siteprep)
	$(MAKE) keys upgrade product checksum
